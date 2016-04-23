import java.util.List;

class Util {

  public static byte[] multiplicationGF(byte[] bytes){
      byte[] multi = new byte[bytes.length];

      //check if byte[0] < 0 (means negative), then the multiplication will cause it to have carry
      boolean carry = bytes[0] < 0;

      for(int i = 0; i < bytes.length; i++){

          //multiplication by 2 = shift left by 1
          multi[i] = (byte) (bytes[i] << 1);

          //2nd last one
          if(i < bytes.length - 1 && bytes[i + 1] < 0){
              multi[i] = (byte) (multi[i] ^ 1);
          }
      }
      if(carry){
          multi[bytes.length - 1] = (byte) (multi[bytes.length - 1] ^ 135);
      }
      return multi;
  }

  public static byte[] bytesXOR(byte[] x, byte[] y){
    byte[] xor = new byte[x.length];
    for(int i = 0; i < x.length; i++){
        xor[i] = (byte) (x[i] ^ y[i]);
    }
    return xor;
}

  public static byte[] mergeArr(List<byte[]> arrList, int size){
        byte[] merged= new byte[size];
        int i = 0;
        for (byte[] bytes : arrList){
            for(byte elm: bytes){
                if (i >= size) break;
                merged[i] = elm;
                i++;
            }
        }
        return merged;
    }

  public static void printArrBytes(byte[] byteArr){
        for (byte x : byteArr){
          System.out.print(x + " ");
        }
        System.out.println();
    }
}
