import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;

public class XTSAES {

  public static void main(String[] args) throws Exception {
    // asumsi plaintext adalah ascii character
    File file = new File("plaintext.txt");
    long plaintextByteLength = file.length();
    int arrPlaintextByteSize = (int) (long) ((plaintextByteLength / 16) + 1);
    int lastByteLength = (int) (long) (plaintextByteLength % 16);

    FileInputStream fis = new FileInputStream(file);

    // asumsi key adalah ascii character, bukan hex (seharusnya hex)
    File fileKey = new File("key.txt");
    FileInputStream fisKey = new FileInputStream(fileKey);

    // TO DO: convert key dari ascii jadi hex, lalu hex jadi binary

    // Partitioning key to key1 and key2
    byte[] key1 = new byte[16];
    byte[] key2 = new byte[16];
    fisKey.read(key1);
    fisKey.read(key2);

    byte[][] arrPlaintextByte = new byte[arrPlaintextByteSize][16];

    for (int i = 0; i < arrPlaintextByteSize; i++){
      fis.read(arrPlaintextByte[i]);
    }

    byte[] tweak = new byte[16];
    byte[][] arrCiphertextByte = XTSAESEnc(key1, key2, arrPlaintextByte, lastByteLength, tweak);

    for (int i = 0; i < arrPlaintextByteSize; i++){
      for (int j = 0; j < 16; j++){
          byte b1 = arrCiphertextByte[i][j];
          String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');
          System.out.print(s1);
      }
    }

    byte[][] arrResultByte = XTSAESDec(key1, key2, arrPlaintextByte, lastByteLength, tweak);
    // while ((byteCount = fis.read(arr)[i]) > 0){
    //   System.out.println(byteCount);
    //   byte[] ciphertext = encryptAES(arr, key1);
    //   System.out.println(DatatypeConverter.printHexBinary(ciphertext));
    //   arr = new byte[16];
    // }

  }

  /**
  * XTS-AES-blockEnc procedure, encryption of a single 128-bit block
  *
  */
  public static byte[][] XTSAESEnc(byte[] key1, byte[] key2, byte[][] arrPlaintextByte, int lastByteLength, byte[] i) throws Exception {
    // P --> P[0], P[1],.., P[m-1], P[m] --> udh oke
    // key1 = .. --> udh oke
    // key2 = .. --> udh oke

    // for q=0:m-2 do {
    //   C[q] = XTSAESBlockEnc(key1, key2, plaintext, i, q);
    // }

    int m = arrPlaintextByte.length - 1; // get index of last element
    byte[][] arrCiphertextByte = new byte[arrPlaintextByte.length][16];
    for (int q = 0; q <= (m-2); q++){
      arrCiphertextByte[q] = XTSAESBlockEnc(key1, key2, arrPlaintextByte[q], i, q);
    }

    // b = bit-size of P[m] --> udah oke, tinggal diubah dari byte ke bit?
    int b = lastByteLength * 8;

    // if b == 0 {
    //   C[m-1] = XTSAESBlockEnc(key1, key2,P[m-1], i, m-1)
    //   C[m] = null
    // } else{
    //   CC = XTSAESBlockEnc(key1, key2, P[m-1], i, m-1)
    //   C[m] = first b bits of CC
    //   CP = last (128-b) bits pf CC
    //   PP = P[m] concate CP
    //   C[m-1] = XTSAESBlockEnc(key1, key2, PP, i, m)
    // }

    if (b == 0){
      arrCiphertextByte[m-1] = XTSAESBlockEnc(key1, key2, arrPlaintextByte[m-1], i, (m-1));
      arrCiphertextByte[m] = null;
    } else{
      byte[] CC = XTSAESBlockEnc(key1, key2, arrPlaintextByte[m-1], i, (m-1));

      for (int count = 0; count < lastByteLength; count++){
        arrCiphertextByte[m][count] = CC[count];
      }

      byte[] CP = new byte[16];
      byte[] PP = new byte[16];
      for (int count = lastByteLength; count < 16; count++){
          CP[count] = CC[count];
          PP[count] = CC[count];
      }

      // byte[] PP = arrCiphertextByte[m] + CP
      // PP already has CP here, tinggal masukkin arrPlaintextByte[m]
      for (int count = 0; count < lastByteLength; count++){
        PP[count] = arrPlaintextByte[m][count];
      }

      arrCiphertextByte[m-1] = XTSAESBlockEnc(key1, key2, PP, i, m);

    }

    return arrCiphertextByte;
  }

  public static byte[] XTSAESBlockEnc(byte[] key1, byte[] key2, byte[] plaintext, byte[] i, int j) throws Exception {
    // T = encryptAES(key2, i) (x) alfa^j  --> cari tahu gimana cara modular multiplication
    // byte[] alfa = a^j;
    byte[] T = multiplicationByAlpha(j, encryptAES(key2, i));
    // PP = P xor T --> cari tahu gimana caranya xor di java?
    // byte[] PP = plaintext ^ T;
    byte[] PP = new byte[16];
    for (int a = 0; a < PP.length; a++) {
        PP[a] = (byte) (plaintext[a] ^ T[a]);
    }
    // CC = encryptAES(key1, PP)
    byte[] CC = encryptAES(key1, PP);
    // C = CC xor T
    //byte[] C = CC ^ T;
    byte[] C = new byte[16];
    for (int a = 0; a < C.length; a++) {
        C[a] = (byte) (CC[a] ^ T[a]);
    }
    // return C;
    return C;
  }

  public static byte[] multiplicationByAlpha(int j, byte[] a) throws Exception {

    for(int i = 1; i < j; i++){
      a[0] = (byte) ((2*(a[0] % 128)) ^ (135*(a[15] / 128)));
      for(int k = 1; k <= 15; k++){
        a[k] = (byte) ((2*(a[k] % 128)) ^ (a[k-1] / 128));
      }
    }
    return a;
  }

  public static byte[] hexToBinary(String stringHex) throws Exception {
    return DatatypeConverter.parseHexBinary(stringHex); //ubah dari hex jadi binary
  }

  public static byte[] stringToBinary(String string) throws Exception {
    return string.getBytes("UTF-8"); // ubah dari raw string to binary
  }

  /**
  * Method untuk encryptAES
  * parameter: byte[] plaintext (byte), byte[] keyBinary (byte)
  * output: byte[] ciphertext (byte)
  */
  public static byte[] encryptAES(byte[] plaintext, byte[] keyBinary) throws Exception {
    SecretKey key = new SecretKeySpec(keyBinary, "AES");
    Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] result = cipher.doFinal(plaintext);
    return result;
  }

  /**
  * Method untuk encryptAES
  * parameter: byte[] ciphertext (byte), byte[] keyBinary (byte)
  * output: byte[] plaintext (byte)
  */
  public static byte[] decryptAES(byte[] ciphertext, byte[] keyBinary) throws Exception {
    SecretKey key = new SecretKeySpec(keyBinary, "AES");
    Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
    cipher.init(Cipher.DECRYPT_MODE, key);
    byte[] plaintext = cipher.doFinal(ciphertext);
    return plaintext;
  }
}
