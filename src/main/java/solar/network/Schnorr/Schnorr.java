package solar.network.Schnorr;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Arrays;

public class Schnorr    {

    public static byte[] sign(byte[] msg, byte[] secKey, byte[] auxRand) throws Exception    {
        if(msg.length != 32)    {
            throw new Exception("The message must be a 32-byte array.");
        }
        BigInteger secKey0 = Util.bigIntFromBytes(secKey);

        if(!(BigInteger.ONE.compareTo(secKey0) <= 0 && secKey0.compareTo(Point.getn().subtract(BigInteger.ONE)) <= 0)) {
            throw new Exception("The secret key must be an integer in the range 1..n-1.");
        }
        Point P = Point.mul(Point.getG(), secKey0);
        if(!P.hasEvenY())    {
            secKey0 = Point.getn().subtract(secKey0);
        }
        int len = Util.bytesFromBigInteger(secKey0).length + P.toBytes().length + msg.length;
        byte[] buf = new byte[len];
        byte[] t = Util.xor(Util.bytesFromBigInteger(secKey0), Point.taggedHash("BIP0340/aux", auxRand));
        System.arraycopy(t, 0, buf, 0, t.length);
        System.arraycopy(P.toBytes(), 0, buf, t.length, P.toBytes().length);
        System.arraycopy(msg, 0, buf, t.length + P.toBytes().length, msg.length);
        BigInteger k0 = Util.bigIntFromBytes(Point.taggedHash("BIP0340/nonce", buf)).mod(Point.getn());
        if(k0.compareTo(BigInteger.ZERO) == 0)    {
            throw new Exception("Failure. This happens only with negligible probability.");
        }
        Point R = Point.mul(Point.getG(), k0);
        BigInteger k = null;
        if(!R.hasEvenY())    {
            k = Point.getn().subtract(k0);
        }
        else    {
            k = k0;
        }
        len = R.toBytes().length + P.toBytes().length + msg.length;
        buf = new byte[len];
        System.arraycopy(R.toBytes(), 0, buf, 0, R.toBytes().length);
        System.arraycopy(P.toBytes(), 0, buf, R.toBytes().length, P.toBytes().length);
        System.arraycopy(msg, 0, buf, R.toBytes().length + P.toBytes().length, msg.length);
        BigInteger e = Util.bigIntFromBytes(Point.taggedHash("BIP0340/challenge", buf)).mod(Point.getn());
        BigInteger kes = k.add(e.multiply(secKey0)).mod(Point.getn());
        len = R.toBytes().length + Util.bytesFromBigInteger(kes).length;
        byte[] sig = new byte[len];
        System.arraycopy(R.toBytes(), 0, sig, 0, R.toBytes().length);
        System.arraycopy(Util.bytesFromBigInteger(kes), 0, sig, R.toBytes().length, Util.bytesFromBigInteger(kes).length);
        if(!verify(msg, P.toBytes(), sig))    {
            throw new Exception("The signature does not pass verification.");
        }
        return sig;
    }

    public static boolean verify(byte[] msg, byte[] pubkey, byte[] sig) throws Exception    {
        if(msg.length != 32)    {
            throw new Exception("The message must be a 32-byte array.");
        }
        if(pubkey.length != 32)    {
            throw new Exception("The public key must be a 32-byte array.");
        }
        if(sig.length != 64)    {
            throw new Exception("The signature must be a 64-byte array.");
        }

        Point P = Point.liftX(pubkey);
        if(P == null)    {
            return false;
        }
        BigInteger r = Util.bigIntFromBytes(Arrays.copyOfRange(sig,0, 32));
        BigInteger s = Util.bigIntFromBytes(Arrays.copyOfRange(sig,32, 64));
        if(r.compareTo(Point.getp()) >= 0 || s.compareTo(Point.getn()) >= 0)    {
            return false;
        }
        int len = 32 + pubkey.length + msg.length;
        byte[] buf = new byte[len];
        System.arraycopy(sig, 0, buf, 0, 32);
        System.arraycopy(pubkey, 0, buf, 32, pubkey.length);
        System.arraycopy(msg, 0, buf, 32 + pubkey.length, msg.length);
        BigInteger e = Util.bigIntFromBytes(Point.taggedHash("BIP0340/challenge", buf)).mod(Point.getn());
        Point R = Point.add(Point.mul(Point.getG(), s), Point.mul(P, Point.getn().subtract(e)));
        if(R == null || !R.hasEvenY() || R.getX().compareTo(r) != 0)    {
            return false;
        }else{
            return true;
        }
    }


    public static void main(String[] args) throws Exception {

        final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
        final ECDomainParameters CURVE = new ECDomainParameters(
                CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
        final BigInteger HALF_CURVE_ORDER = CURVE_PARAMS.getN().shiftRight(1);
        String privateKey = "C90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B14E5C9";
        String publicKey = "DD308AFEC5777E13121FA72B9CC1B7CC0139715309B086C960E18FD969774EB8";

        String aux_rand = "C87AA53824B4D7AE2EB035A2B5BBBCCC080E76CDC6D1692C4B0B62D798E6D906";

        String message = "7E2D58D8B3BCDF1ABADEC7829054F90DDA9805AAB56C77333024B9D0A508B75C";

        String sinature = "5831AAEED7B44BB74E5EAB94BA9D4294C49BCF2A60728D8B4C200F50DD313C1BAB745879A5AD954A72C45A91C3A51D3C7ADEA98D82F8481E0E1E03674A6F3FB7";

        byte[] result = Schnorr.sign(Hex.decode(message),Hex.decode(privateKey),Hex.decode(aux_rand));
        //byte[] result = Schnorr.sign(Hex.decode(message),priKeyBytes,Hex.decode(aux_rand));
        if(Hex.toHexString(result).equalsIgnoreCase(sinature)){
            System.out.println("true");
        }else{
            System.out.println("false");
        }
        boolean verify = Schnorr.verify(Hex.decode(message),Hex.decode(publicKey),Hex.decode(sinature));
        if(verify){
            System.out.println("true");
        }else{
            System.out.println("false");
        }

    }

}
