package solar.network.Schnorr;

import org.bitcoinj.core.Sha256Hash;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

public class KeyUtil {

    private final static String TAPTWEAK = "TapTweak";


    public   static  byte[] getTweakedPrivKey(byte[] originalPrivKey) throws Exception {
        return getTweakedPrivKey(originalPrivKey,null);
    }

    public   static  byte[] getTweakedPrivKey(byte[] originalPrivKey, @Nullable byte[] hash) throws Exception {
        BigInteger privKey0 = new BigInteger(1, originalPrivKey);
        Point privPoint = Point.mul(Point.getG(), privKey0);
        BigInteger privKey;
        if (privPoint.hasEvenY()) {
            privKey = privKey0;
        } else {
            privKey = Point.getn().subtract(privKey0);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(32);
        byte[] tag = Sha256Hash.hash(TAPTWEAK.getBytes());
        bos.write(tag);
        bos.write(tag);
        bos.write(privPoint.toBytes());
        if (hash != null) {
            bos.write(hash);
        }
        byte[] tweak = Sha256Hash.hash(bos.toByteArray());
        return Util.bytesFromBigInteger((privKey.add(new BigInteger(1,tweak))).mod(Point.getn()));
    }

    public   static  byte[] getTweakedPubKey(byte[] privKey) throws Exception {
        return Point.genPubKey(privKey);
    }
}
