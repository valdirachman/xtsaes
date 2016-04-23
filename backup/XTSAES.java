import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;


public class XTSAES {

  public static final int BLOCK_SIZE = 16;

  public static void main(String[] args) throws Exception {
    // asumsi plaintext adalah ascii character
    //******** FILE INPUT *********
    File file = new File("IMG_7112.JPG");
    int plaintextByteLength = (int) file.length();
    FileInputStream fis = new FileInputStream(file);

    //Key input adalah hex
    //********* KEY *********
    File fileKey = new File("key.txt");
    FileInputStream fisKey = new FileInputStream(fileKey);
    BufferedReader r = new BufferedReader(new FileReader(fileKey));
    String keyhex = r.readLine();
    byte[] keys = DatatypeConverter.parseHexBinary(keyhex);

    System.out.println("KEYS = ");
    printArrBytes(keys);

    byte[] key1 = Arrays.copyOfRange(keys, 0, BLOCK_SIZE);
    byte[] key2 = Arrays.copyOfRange(keys, BLOCK_SIZE, BLOCK_SIZE*2);

    System.out.print("key1 = ");
    printArrBytes(key1);

    System.out.print("key2 = ");
    printArrBytes(key2);

    //********* HARD CODE TWEAK *********
    byte[] tweak = {
      1,  2,  3,  4,  5,  6,  7,  8,
      9, 10, 11, 12, 13, 14, 15, 16,
    };

    tweak = encryptAES(tweak,key2);
    System.out.print("Encrypted TWEAK:");
    printArrBytes(tweak);


    //****************** ENCRYPTION ******************
    byte[] hasilEnkrip = XTSAESEnc2(key1, key2, fis, tweak, plaintextByteLength);
    System.out.println("--  ENCRYPT --");
    printArrBytes(hasilEnkrip);

    FileOutputStream writer = new FileOutputStream("hasil.JPG");
    writer.write(hasilEnkrip);
    writer.flush();
    writer.close();

    //****************** DECRYPTION ******************
    //get source
    file = new File("hasil.JPG");
    int ciphertextByteLength = (int)file.length();
    FileInputStream fisDek = new FileInputStream (file);
    byte[] resultDekripsi = XTSAESDec2(key1,key2, fisDek,tweak, ciphertextByteLength);
    System.out.println("--  DECRYPT --");
    printArrBytes(resultDekripsi);

    writer = new FileOutputStream("hasilDekrip.JPG");
    writer.write(resultDekripsi);
    writer.flush();
    writer.close();

  }


//  public static byte[][] XTSAESEnc(byte[] key1, byte[] key2, byte[][] arrPlaintextByte, int lastByteLength, byte[] tweakJ)
  public static byte[] XTSAESEnc2(byte[] key1, byte[] key2, InputStream plainText, byte[] tweak, int plaintextByteLength) throws Exception{
          ArrayList<byte[]> result = new ArrayList<>();
          byte[] plainArrByte = new byte[BLOCK_SIZE];
          byte[] cipherArrByte = null;
          byte[] firstNthCipher = null, lastNthCipher, firstXOR, encrypted;
          byte[] tweakJ = Arrays.copyOf(tweak, BLOCK_SIZE);
          int length;

          while((length = plainText.read(plainArrByte)) > 0){

              if(length < BLOCK_SIZE){
                  firstNthCipher = Arrays.copyOfRange(cipherArrByte, 0, length);
                  lastNthCipher = Arrays.copyOfRange(cipherArrByte, length, BLOCK_SIZE);

                  for (int i = 0; i < lastNthCipher.length; i++){
                      //take lastnthcipher, put to plainArr
                      plainArrByte[i+length] = lastNthCipher[i];
                  }
              }

              if(cipherArrByte != null) result.add(cipherArrByte);

              //XTS AES per block encryption
              firstXOR = bytesXOR(plainArrByte, tweakJ);
              encrypted = encryptAES(firstXOR,key1);
              cipherArrByte = bytesXOR(encrypted, tweakJ);  //the 2nd XOR

              //means the last
              if(length < BLOCK_SIZE){
                  result.set(result.size()-1, cipherArrByte);
                  cipherArrByte = firstNthCipher;
                  break;
              }
              tweakJ = multiplicationGF(tweakJ);
          }

          //put last cipher -> firstNthCipher
          if(cipherArrByte != null){
            result.add(cipherArrByte);
          }

          return mergeArr(result, plaintextByteLength);
  }

  /**

  * XTS-AES-blockEnc procedure, encryption of a single 128-bit block
  *
  */
  // public static byte[][] XTSAESEnc(byte[] key1, byte[] key2, byte[][] arrPlaintextByte, int lastByteLength, byte[] tweakJ) throws Exception {
  //
  //   //System.out.println("lastByteLength = " + lastByteLength);
  //
  //   int m = arrPlaintextByte.length - 1; // get index of last element
  //   byte[][] arrCiphertextByte = new byte[arrPlaintextByte.length][16];
  //
  //   //System.out.println("Print cipher hasil enkrip dengan m-2 = " + (m-2));
  //
  //
  //   /*
  //
  //   byte[] tweakedPlain = bytesXOR(plain, tweakJ);
  //
  //   byte[] AESResult = encryptAES(tweakedPlain,key1);
  //   cipher = bytesXOR(AESResult, tweakJ);
  //
  //   */
  //
  //   for (int q = 0; q <= (m-2); q++){
  //     arrCiphertextByte[q] = XTSAESBlockEnc(key1, key2, arrPlaintextByte[q], tweakJ, q);
  //     tweakJ = multiplicationGF(tweakJ);
  //     //String x = new String(arrCiphertextByte[q]);
  //     //System.out.println(x);
  //   }
  //
  //
  //   // b = bit-size of P[m] --> udah oke, tinggal diubah dari byte ke bit?
  //   int b = lastByteLength * 8;
  //
  //   // if b == 0 {
  //   //   C[m-1] = XTSAESBlockEnc(key1, key2,P[m-1], i, m-1)
  //   //   C[m] = null
  //   // } else{
  //   //   cc = XTSAESBlockEnc(key1, key2, P[m-1], i, m-1)
  //   //   C[m] = first b bits of cc
  //   //   cp = last (128-b) bits pf cc
  //   //   pp = P[m] concate cp
  //   //   C[m-1] = XTSAESBlockEnc(key1, key2, pp, i, m)
  //   // }
  //
  //   if (b == 0){
  //     arrCiphertextByte[m-1] = XTSAESBlockEnc(key1, key2, arrPlaintextByte[m-1], tweakJ, (m-1));
  //     arrCiphertextByte[m] = null;
  //     System.out.println("kelipatan 16");
  //   } else{
  //     System.out.println("hasil modulo != 0");
  //
  //     byte[] cc = XTSAESBlockEnc(key1, key2, arrPlaintextByte[m-1], tweakJ, (m-1));
  //
  //     for (int count = 0; count < lastByteLength; count++){
  //       arrCiphertextByte[m][count] = cc[count];
  //     }
  //
  //     byte[] cp = new byte[16];
  //     byte[] pp = new byte[16];
  //     for (int count = lastByteLength; count < 16; count++){
  //         cp[count] = cc[count];
  //         pp[count] = cc[count];
  //     }
  //
  //     tweakJ = multiplicationGF(tweakJ);
  //
  //     // byte[] pp = arrCiphertextByte[m] + cp
  //     // pp already has cp here, tinggal masukkin arrPlaintextByte[m]
  //     for (int count = 0; count < lastByteLength; count++){
  //       pp[count] = arrPlaintextByte[m][count];
  //     }
  //
  //     arrCiphertextByte[m-1] = XTSAESBlockEnc(key1, key2, pp, tweakJ, m);
  //
  //   }
  //
  //   return arrCiphertextByte;
  // }


  public static byte[] XTSAESDec2(byte[] key1, byte[] key2, InputStream cipherText, byte[] tweak, int ciphertextByteLength) throws Exception{

          System.out.println("key1:");
          printArrBytes(key1);
          System.out.println("key2");
          printArrBytes(key2);

          ArrayList<byte[]> result = new ArrayList<>();
          byte[] firstNthCipher = null, lastNthCipher = null;
          byte[] tweakJMin1 = null;
          byte[] tweakJ = Arrays.copyOf(tweak, BLOCK_SIZE);
          byte[] cipherArrByte = new byte[BLOCK_SIZE];
          byte[] nextCipher = new byte[BLOCK_SIZE];
          byte[] plainArrByte = null;
          byte[] firstXOR, decripted;

          int length = cipherText.read(cipherArrByte);

          int nextLength;

          if((nextLength = cipherText.read(nextCipher)) > 0 && nextLength < BLOCK_SIZE){
              tweakJMin1 = tweakJ;
              tweakJ = multiplicationGF(tweakJ);
          }

          while(length > 0){
              if(length < BLOCK_SIZE){
                  firstNthCipher = Arrays.copyOfRange(plainArrByte, 0, length);
                  lastNthCipher = Arrays.copyOfRange(plainArrByte, length, BLOCK_SIZE);
                  for (int i = 0; i < lastNthCipher.length; i++){
                      cipherArrByte[i + length] = lastNthCipher[i];
                  }
                  tweakJ = tweakJMin1;
              }

              if(plainArrByte != null) result.add(plainArrByte);

              firstXOR = bytesXOR(cipherArrByte, tweakJ);
              decripted = decryptAES(firstXOR,key1);
              plainArrByte = bytesXOR(decripted, tweakJ);

              if(length < BLOCK_SIZE){
                  result.set(result.size()-1, plainArrByte);
                  plainArrByte = firstNthCipher;
                  break;
              }

              //calculate new T
              tweakJ = multiplicationGF(tweakJ);

              //read again
              cipherArrByte = Arrays.copyOf(nextCipher, nextCipher.length);
              length = nextLength;
              nextLength = cipherText.read(nextCipher);
              if(nextLength > 0 && nextLength < BLOCK_SIZE){
                  tweakJMin1 = tweakJ;
                  tweakJ = multiplicationGF(tweakJ);
              }
          }
          //last input
          if(plainArrByte != null) result.add(plainArrByte);
          return mergeArr(result, ciphertextByteLength);
      }

  // public static byte[][] XTSAESDec(byte[] key1, byte[] key2, byte[][] arrCiphertextByte, int lastByteLength, byte[] i) throws Exception {
  //   // P --> P[0], P[1],.., P[m-1], P[m] --> udh oke
  //   // key1 = .. --> udh oke
  //   // key2 = .. --> udh oke
  //
  //   // for q=0:m-2 do {
  //   //   C[q] = XTSAESBlockEnc(key1, key2, plaintext, i, q);
  //   // }
  //
  //   int m = arrCiphertextByte.length - 1; // get index of last element
  //   byte[][] arrPlaintextByte = new byte[arrCiphertextByte.length][16];
  //   for (int q = 0; q <= (m-2); q++){
  //     arrPlaintextByte[q] = XTSAESBlockDec(key1, key2, arrCiphertextByte[q], i, q);
  //
  //   }
  //
  //   // b = bit-size of P[m] --> udah oke, tinggal diubah dari byte ke bit?
  //   int b = lastByteLength * 8;
  //
  //   if (b == 0){
  //     arrPlaintextByte[m-1] = XTSAESBlockDec(key1, key2, arrCiphertextByte[m-1], i, (m-1));
  //     arrPlaintextByte[m] = null;
  //   } else{
  //     byte[] pp = XTSAESBlockDec(key1, key2, arrCiphertextByte[m-1], i, m);
  //
  //     for (int count = 0; count < lastByteLength; count++){
  //       arrPlaintextByte[m][count] = pp[count];
  //     }
  //
  //     byte[] cp = new byte[16];
  //     byte[] cc = new byte[16];
  //     for (int count = lastByteLength; count < 16; count++){
  //         cp[count] = pp[count];
  //         cc[count] = pp[count];
  //     }
  //
  //     // byte[] pp = arrCiphertextByte[m] + cp
  //     // pp already has cp here, tinggal masukkin arrPlaintextByte[m]
  //     for (int count = 0; count < lastByteLength; count++){
  //       cc[count] = arrCiphertextByte[m][count];
  //     }
  //
  //     arrPlaintextByte[m-1] = XTSAESBlockDec(key1, key2, cc, i, (m-1));
  //
  //   }
  //
  //   return arrPlaintextByte;
  // }
  //
  // //t tadinya i
  // public static byte[] XTSAESBlockEnc(byte[] key1, byte[] plaintext, byte[] t) throws Exception {
  //   // T = encryptAES(key2, i) (x) alfa^j  --> cari tahu gimana cara modular multiplication
  //   // byte[] alfa = a^j;
  //
  //   //byte[] t = multiplicationByAlpha(j, encryptAES(key2, i));
  //   //byte[] t = multiplicationGF(encryptAES(key2,i));
  //
  //   // pp = P xor T --> cari tahu gimana caranya xor di java?
  //   // byte[] pp = plaintext ^ T;
  //   byte[] pp = new byte[16];
  //   for (int a = 0; a < pp.length; a++) {
  //       pp[a] = (byte) (plaintext[a] ^ t[a]);
  //   }
  //   // cc = encryptAES(key1, pp)
  //   byte[] cc = encryptAES(key1, pp);
  //   // C = cc xor T
  //   //byte[] C = cc ^ T;
  //   byte[] c = new byte[16];
  //   for (int a = 0; a < c.length; a++) {
  //       c[a] = (byte) (cc[a] ^ t[a]);
  //   }
  //   // return C;
  //   return c;
  // }
  //
  // public static byte[] XTSAESBlockDec(byte[] key1, byte[] key2, byte[] ciphertext, byte[] t, int j) throws Exception {
  //   // T = encryptAES(key2, i) (x) alfa^j  --> cari tahu gimana cara modular multiplication
  //   // byte[] alfa = a^j;
  //   //byte[] t = multiplicationByAlpha(j, encryptAES(key2, i));
  //
  //   // cc = C xor T
  //   byte[] cc = new byte[16];
  //   for (int a = 0; a < cc.length; a++) {
  //       cc[a] = (byte) (ciphertext[a] ^ t[a]);
  //   }
  //
  //   // pp = decryptAES(key1, cc)
  //   byte[] pp = decryptAES(key1, cc);
  //   // P = pp xor T
  //   byte[] p = new byte[16];
  //   for (int a = 0; a < p.length; a++) {
  //       p[a] = (byte) (pp[a] ^ t[a]);
  //   }
  //
  //   // return P
  //   return p;
  // }
  //
  // // public static byte[] multiplicationByAlpha(int j, byte[] a) throws Exception {
  // //
  // //
  // //   //System.out.println("masuk");
  // //   byte[][] arr = new byte[j+2][16];
  // //   arr[0] = a;
  // //
  // //   for(int i = 0; i <= j; i++){
  // //     // byte[] temp = new byte[16];
  // //     // System.arraycopy(a, 0, temp, 0, a.length );
  // //
  // //     arr[i+1][0] = (byte) ((2*(arr[i][0] % 128)) ^ (135*(arr[i][15] / 128)));
  // //
  // //     for(int k = 1; k <= 15; k++){
  // //       arr[i+1][k] = (byte) ((2*(arr[i][k] % 128)) ^ (arr[i][k-1] / 128));
  // //     }
  // //   }
  // //   return arr[j];
  // // }
  //
  // public static byte[] hexToBinary(String stringHex) throws Exception {
  //   return DatatypeConverter.parseHexBinary(stringHex); //ubah dari hex jadi binary
  // }
  //
  // public static byte[] stringToBinary(String string) throws Exception {
  //   return string.getBytes("UTF-8"); // ubah dari raw string to binary
  // }

  /**
  * Method untuk encryptAES
  * parameter: byte[] plaintext (byte), byte[] keyBinary (byte)
  * output: byte[] ciphertext (byte)
  */
  public static byte[] encryptAES(byte[] plaintext, byte[] keyBinary) throws Exception {
    SecretKey key = new SecretKeySpec(keyBinary, "AES");
    Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return cipher.doFinal(plaintext);
    //String x = new String(result);
    //System.out.println(x);
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
    //String x = new String(plaintext);
    //System.out.println(x);
    return plaintext;
  }

  public static byte[] multiplicationGF(byte[] bytes){
      byte[] result = new byte[bytes.length];
      boolean isCarry = bytes[0] < 0;
      for(int i = 0; i < bytes.length; i++){
          result[i] = (byte) (bytes[i] << 1);

          if(i < bytes.length - 1 && bytes[i + 1] < 0){
              result[i] = (byte) (result[i] ^ 1);
          }
      }
      if(isCarry){
          result[bytes.length - 1] = (byte) (result[bytes.length - 1] ^ 135);
      }
      return result;
  }

  public static byte[] bytesXOR(byte[] a, byte[] b){
    byte[] result = new byte[a.length];
    for(int i = 0; i < a.length; i++){
        result[i] = (byte) (a[i] ^ b[i]);
    }
    return result;
}

  private static byte[] mergeArr(List<byte[]> list, int size){
//        byte[] result= new byte[(list.size() * BLOCK_SIZE)];
        byte[] result= new byte[size];
        int i = 0;
        for (byte[] bytes : list){
            for(byte elm: bytes){
                if (i >= size) break;
                result[i] = elm;
                i++;
            }
        }
        return result;
    }

    public static void printArrBytes(byte[] bytes){
        for (byte elm : bytes){
          System.out.print(elm + " ");
        }
        System.out.println();
    }

}
