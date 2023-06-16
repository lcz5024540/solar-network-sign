package solar.network.Schnorr;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Util {

  public static byte[] bytesFromBigInteger(BigInteger n) {
      return bigIntegerToBytes(n,32);
  }

  public static BigInteger bigIntFromBytes(byte[] b) {
      return new BigInteger(1, b);
  }


    public static byte[] bigIntegerToBytes(BigInteger b, int numBytes) {
        byte[] src = b.toByteArray();
        byte[] dest = new byte[numBytes];
        boolean isFirstByteOnlyForSign = src[0] == 0;
        int length = isFirstByteOnlyForSign ? src.length - 1 : src.length;
        int srcPos = isFirstByteOnlyForSign ? 1 : 0;
        int destPos = numBytes - length;
        System.arraycopy(src, srcPos, dest, destPos, length);
        return dest;
    }

    public static byte[] xor(byte[] b0, byte[] b1)   {

        if(b0.length != b1.length)   {
            return  null;
        }

        byte[] ret = new byte[b0.length];
        int i = 0;
        for (byte b : b0)   {
            ret[i] = (byte)(b ^ b1[i]);
            i++;
        }

        return ret;
    }

    public static byte[] bigInteger2Bytes(BigInteger a){
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(a.longValue());
        return buffer.array();
    }

}
