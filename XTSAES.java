import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;

public class XTSAES {

  public static void main(String[] args) throws Exception {
    File file = new File("plaintext.txt");
    FileInputStream fis = new FileInputStream(file);

    File fileKey = new File("key.txt");
    FileInputStream fisKey = new FileInputStream(fileKey);

    byte[] key1 = new byte[16];
    byte[] key2 = new byte[16];
    fisKey.read(key1);
    fisKey.read(key2);


    byte[] arr = new byte[16];
    while (fis.read(arr) > 0){
      byte[] ciphertext = encryptAES(arr, keyBinary);
      System.out.println(DatatypeConverter.printHexBinary(ciphertext));
      
      arr = new byte[16];
    }

  }

  public static byte[] hexToBinary(String stringHex){
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
