import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;

public class XTSAESBackup {

  public static void main(String[] args) throws Exception {
    // asumsi plaintext adalah ascii character
    File file = new File("plaintext.txt");
    long plaintextByteLength = file.length();
    System.out.println("length = " + file.length());
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

    //byte[] tweak = Util.hex2byte("12345678901234567890123456789012");
    byte[] tweak = {
      1,  2,  3,  4,  5,  6,  7,  8,
      9, 10, 11, 12, 13, 14, 15, 16,
    };

    //2 dim array
    byte[][] arrCiphertextByte = XTSAESEnc(key1, key2, arrPlaintextByte, lastByteLength, tweak);

    // for (int i = 0; i < arrPlaintextByteSize; i++){
    //   for (int j = 0; j < 16; j++){
    //       byte b1 = arrCiphertextByte[i][j];
    //       String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');
    //       System.out.print(s1);
    //   }
    // }

    System.out.println("--  ENCRYPT --");


    for (int i = 0; i < arrCiphertextByte.length; i++){
      //String s = new String(arrResultByte[i]);
      for (int j = 0; j < 16; j++){
          byte b1 = arrCiphertextByte[i][j];
          System.out.print(b1+" ");
      }
    }


    System.out.println("");
    System.out.println("--  DECRYPT --");

    byte[][] arrResultByte = XTSAESDec(key1, key2, arrCiphertextByte, lastByteLength, tweak);
    for (int i = 0; i < arrResultByte.length; i++){
      //String s = new String(arrResultByte[i]);
      for (int j = 0; j < 16; j++){
          byte b1 = arrResultByte[i][j];
          System.out.print(b1);
      }
    }
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

    System.out.println("lastByteLength = " + lastByteLength);
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
    //   cc = XTSAESBlockEnc(key1, key2, P[m-1], i, m-1)
    //   C[m] = first b bits of cc
    //   cp = last (128-b) bits pf cc
    //   pp = P[m] concate cp
    //   C[m-1] = XTSAESBlockEnc(key1, key2, pp, i, m)
    // }

    if (b == 0){
      arrCiphertextByte[m-1] = XTSAESBlockEnc(key1, key2, arrPlaintextByte[m-1], i, (m-1));
      arrCiphertextByte[m] = null;
      System.out.println("kelipatan 16");
    } else{
      System.out.println("hasil modulo != 0");

      byte[] cc = XTSAESBlockEnc(key1, key2, arrPlaintextByte[m-1], i, (m-1));

      for (int count = 0; count < lastByteLength; count++){
        arrCiphertextByte[m][count] = cc[count];
      }

      byte[] cp = new byte[16];
      byte[] pp = new byte[16];
      for (int count = lastByteLength; count < 16; count++){
          cp[count] = cc[count];
          pp[count] = cc[count];
      }

      // byte[] pp = arrCiphertextByte[m] + cp
      // pp already has cp here, tinggal masukkin arrPlaintextByte[m]
      for (int count = 0; count < lastByteLength; count++){
        pp[count] = arrPlaintextByte[m][count];
      }

      arrCiphertextByte[m-1] = XTSAESBlockEnc(key1, key2, pp, i, m);

    }

    return arrCiphertextByte;
  }

  public static byte[][] XTSAESDec(byte[] key1, byte[] key2, byte[][] arrCiphertextByte, int lastByteLength, byte[] i) throws Exception {
    // P --> P[0], P[1],.., P[m-1], P[m] --> udh oke
    // key1 = .. --> udh oke
    // key2 = .. --> udh oke

    // for q=0:m-2 do {
    //   C[q] = XTSAESBlockEnc(key1, key2, plaintext, i, q);
    // }

    int m = arrCiphertextByte.length - 1; // get index of last element
    byte[][] arrPlaintextByte = new byte[arrCiphertextByte.length][16];
    for (int q = 0; q <= (m-2); q++){
      arrPlaintextByte[q] = XTSAESBlockDec(key1, key2, arrCiphertextByte[q], i, q);
    }

    // b = bit-size of P[m] --> udah oke, tinggal diubah dari byte ke bit?
    int b = lastByteLength * 8;

    if (b == 0){
      arrPlaintextByte[m-1] = XTSAESBlockDec(key1, key2, arrCiphertextByte[m-1], i, (m-1));
      arrPlaintextByte[m] = null;
    } else{
      byte[] pp = XTSAESBlockDec(key1, key2, arrCiphertextByte[m-1], i, m);

      for (int count = 0; count < lastByteLength; count++){
        arrPlaintextByte[m][count] = pp[count];
      }

      byte[] cp = new byte[16];
      byte[] cc = new byte[16];
      for (int count = lastByteLength; count < 16; count++){
          cp[count] = pp[count];
          cc[count] = pp[count];
      }

      // byte[] pp = arrCiphertextByte[m] + cp
      // pp already has cp here, tinggal masukkin arrPlaintextByte[m]
      for (int count = 0; count < lastByteLength; count++){
        cc[count] = arrCiphertextByte[m][count];
      }

      arrPlaintextByte[m-1] = XTSAESBlockDec(key1, key2, cc, i, (m-1));

    }

    return arrPlaintextByte;
  }

  public static byte[] XTSAESBlockEnc(byte[] key1, byte[] key2, byte[] plaintext, byte[] i, int j) throws Exception {
    // T = encryptAES(key2, i) (x) alfa^j  --> cari tahu gimana cara modular multiplication
    // byte[] alfa = a^j;
    byte[] t = multiplicationByAlpha(j, encryptAES(key2, i));
    // pp = P xor T --> cari tahu gimana caranya xor di java?
    // byte[] pp = plaintext ^ T;
    byte[] pp = new byte[16];
    for (int a = 0; a < pp.length; a++) {
        pp[a] = (byte) (plaintext[a] ^ t[a]);
    }
    // cc = encryptAES(key1, pp)
    byte[] cc = encryptAES(key1, pp);
    // C = cc xor T
    //byte[] C = cc ^ T;
    byte[] c = new byte[16];
    for (int a = 0; a < c.length; a++) {
        c[a] = (byte) (cc[a] ^ t[a]);
    }
    // return C;
    return c;
  }

  public static byte[] XTSAESBlockDec(byte[] key1, byte[] key2, byte[] ciphertext, byte[] i, int j) throws Exception {
    // T = encryptAES(key2, i) (x) alfa^j  --> cari tahu gimana cara modular multiplication
    // byte[] alfa = a^j;
    byte[] t = multiplicationByAlpha(j, encryptAES(key2, i));

    // cc = C xor T
    byte[] cc = new byte[16];
    for (int a = 0; a < cc.length; a++) {
        cc[a] = (byte) (ciphertext[a] ^ t[a]);
    }

    // pp = decryptAES(key1, cc)
    byte[] pp = decryptAES(key1, cc);
    // P = pp xor T
    byte[] p = new byte[16];
    for (int a = 0; a < p.length; a++) {
        p[a] = (byte) (pp[a] ^ t[a]);
    }

    // return P
    return p;
  }

  public static byte[] multiplicationByAlpha(int j, byte[] a) throws Exception {


    System.out.println("masuk");
    byte[][] arr = new byte[j+1][16];
    arr[0] = a;

    for(int i = 1; i <= j; i++){
      // byte[] temp = new byte[16];
      // System.arraycopy(a, 0, temp, 0, a.length );

      arr[i][0] = (byte) ((2*(arr[i-1][0] % 128)) ^ (135*(arr[i-1][15] / 128)));

      for(int k = 1; k <= 15; k++){
        arr[i][k] = (byte) ((2*(arr[i-1][k] % 128)) ^ (arr[i-1][k-1] / 128));
      }
    }
    return arr[j];
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
