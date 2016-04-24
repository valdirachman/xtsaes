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

/*
* Class XTSAES untuk melakukan enrkipsi dan dekripsi AES dalam mode XTS
*/

public class XTSAES {

  //init
  private final int BLOCK_SIZE = 16;
  private File inputFile, keyFile, tweakFile, outputFile;
  private int lengthInput;
  private FileInputStream inputStream;
  private byte[] tweakByte, key1, key2;
  private final byte[] DEFAULT_TWEAK = {
      1,  2,  3,  4,  5,  6,  7,  8,
      9, 10, 11, 12, 13, 14, 15, 16,
    };

  //complete Input
  public XTSAES(File inputFile, File keyFile, File tweakFile, File outputFile){
    this.inputFile = inputFile;
    this.keyFile = keyFile;
    this.tweakFile = tweakFile;
    this.outputFile = outputFile;
    setInput();
    setKey();
    setTweak();
  }

  //for Input without tweak, use default tweak
  public XTSAES(File inputFile, File keyFile, File outputFile){
    this.inputFile = inputFile;
    this.keyFile = keyFile;
    this.outputFile = outputFile;
    setInput();
    setKey();
    setDefaultTweak();
  }

  private void setInput(){
    try{
      this.lengthInput = (int) inputFile.length();
      inputStream = new FileInputStream(inputFile);
    }catch (Exception e) {
              e.printStackTrace();
    }
  }

  private void setKey(){
    try{
      BufferedReader r = new BufferedReader(new FileReader(keyFile));
      String keyhex = r.readLine();
      byte[] keys = DatatypeConverter.parseHexBinary(keyhex);

      this.key1 = Arrays.copyOfRange(keys, 0, BLOCK_SIZE);
      this.key2 = Arrays.copyOfRange(keys, BLOCK_SIZE, BLOCK_SIZE*2);
    }catch (Exception e) {
              e.printStackTrace();
    }

  }

  private void setTweak(){
    try{
      BufferedReader r = new BufferedReader(new FileReader(tweakFile));
      String tweakhex = r.readLine();
      this.tweakByte = DatatypeConverter.parseHexBinary(tweakhex);
      this.tweakByte = encryptAES(tweakByte,this.key2);
    }catch (Exception e) {
              e.printStackTrace();
    }

  }

  private void setDefaultTweak(){
    try{
      this.tweakByte = encryptAES(this.DEFAULT_TWEAK,this.key2);
    }catch (Exception e) {
              e.printStackTrace();
    }
  }

  public void encrypt(){
    try{
      byte[] encrypted = XTSAESEnc();
      FileOutputStream writer = new FileOutputStream(outputFile);
      writer.write(encrypted);
      writer.flush();
      writer.close();
    }catch (Exception e) {
              e.printStackTrace();
    }
    System.out.println("Selesai enkripsi");
  }

  public void decrypt() {
    try{
      byte[] decrypted = XTSAESDec();
      FileOutputStream writer = new FileOutputStream(outputFile);
      writer.write(decrypted);
      writer.flush();
      writer.close();
    }catch (Exception e) {
              e.printStackTrace();
    }
    System.out.println("Selesai dekripsi");
  }

  private byte[] XTSAESEnc() throws Exception{
          FileInputStream plainText = this.inputStream;
          int plaintextByteLength = this.lengthInput;
          ArrayList<byte[]> result = new ArrayList<>();
          byte[] plainArrByte = new byte[BLOCK_SIZE];
          byte[] cipherArrByte = null;
          byte[] firstNthCipher = null, lastNthCipher, firstXOR, encrypted;
          byte[] tweakJ = Arrays.copyOf(tweakByte, BLOCK_SIZE);
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
              firstXOR = Util.bytesXOR(plainArrByte, tweakJ);
              encrypted = encryptAES(firstXOR,key1);
              cipherArrByte = Util.bytesXOR(encrypted, tweakJ);  //the 2nd XOR

              //means the last
              if(length < BLOCK_SIZE){
                  result.set(result.size()-1, cipherArrByte);
                  cipherArrByte = firstNthCipher;
                  break;
              }
              tweakJ = Util.multiplicationGF(tweakJ);
          }

          //put last cipher -> firstNthCipher
          if(cipherArrByte != null){
            result.add(cipherArrByte);
          }

          return Util.mergeArr(result, plaintextByteLength);
  }

  public byte[] XTSAESDec() throws Exception{
          FileInputStream cipherText = this.inputStream;
          int ciphertextByteLength = this.lengthInput;
          ArrayList<byte[]> result = new ArrayList<>();
          byte[] firstNthCipher = null, lastNthCipher = null;
          byte[] tweakJPrevious = null;
          byte[] tweakJNow = Arrays.copyOf(tweakByte, BLOCK_SIZE);
          byte[] cipherArrByte = new byte[BLOCK_SIZE];
          byte[] nextCipher = new byte[BLOCK_SIZE];
          byte[] plainArrByte = null;
          byte[] firstXOR, decripted;

          int length = cipherText.read(cipherArrByte);

          int nextLength;

          if((nextLength = cipherText.read(nextCipher)) > 0 && nextLength < BLOCK_SIZE){
              tweakJPrevious = tweakJNow;
              tweakJNow = Util.multiplicationGF(tweakJNow);
          }

          while(length > 0){
              if(length < BLOCK_SIZE){
                  firstNthCipher = Arrays.copyOfRange(plainArrByte, 0, length);
                  lastNthCipher = Arrays.copyOfRange(plainArrByte, length, BLOCK_SIZE);
                  for (int i = 0; i < lastNthCipher.length; i++){
                      //concate
                      cipherArrByte[i + length] = lastNthCipher[i];
                  }
                  //tweak untuk block terakhir -> tweak J-1 (previous)
                  tweakJNow = tweakJPrevious;
              }

              if(plainArrByte != null) result.add(plainArrByte);

              //XTS AES per block encryption
              firstXOR = Util.bytesXOR(cipherArrByte, tweakJNow);
              decripted = decryptAES(firstXOR, key1);
              plainArrByte = Util.bytesXOR(decripted, tweakJNow);

              //the last one
              if(length < BLOCK_SIZE){
                  result.set(result.size()-1, plainArrByte);
                  plainArrByte = firstNthCipher;
                  break;
              }

              //GFmultiplication tweakJNow for next operation
              tweakJNow = Util.multiplicationGF(tweakJNow);

              //copy nextCipher to cipherArrByte because nextCipher will be used to read input
              cipherArrByte = Arrays.copyOf(nextCipher, nextCipher.length);

              //length = nextLenght ; nextLenght will be changed later
              length = nextLength;

              //if nextLength < BLOCK_SIZE, use tweak (J-1) ->in this case the next iteration will be used tweakJNow
              nextLength = cipherText.read(nextCipher);
              if(nextLength > 0 && nextLength < BLOCK_SIZE){
                  tweakJPrevious = tweakJNow;
                  tweakJNow = Util.multiplicationGF(tweakJNow);
              }
          }
          //last input
          if(plainArrByte != null) result.add(plainArrByte);
          return Util.mergeArr(result, ciphertextByteLength);
      }

  /**
  * Method untuk encryptAES
  * parameter: byte[] plaintext (byte), byte[] keyBinary (byte)
  * output: byte[] ciphertext (byte)
  */
  public byte[] encryptAES(byte[] plaintext, byte[] keyBinary)  {
    byte[] result = null;

    try{
      SecretKey key = new SecretKeySpec(keyBinary, "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
      cipher.init(Cipher.ENCRYPT_MODE, key);
      result = cipher.doFinal(plaintext);
    }
    catch (Exception e) {
            e.printStackTrace();
    }
    return result;
  }

  /**
  * Method untuk encryptAES
  * parameter: byte[] ciphertext (byte), byte[] keyBinary (byte)
  * output: byte[] plaintext (byte)
  */
  public byte[] decryptAES(byte[] ciphertext, byte[] keyBinary) {
    byte[] result = null;
    try{
      SecretKey key = new SecretKeySpec(keyBinary, "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
      cipher.init(Cipher.DECRYPT_MODE, key);
      result = cipher.doFinal(ciphertext);
    }
    catch (Exception e) {
            e.printStackTrace();
    }
    return result;
  }
}
