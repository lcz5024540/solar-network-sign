package solar.network;

import com.google.common.collect.Lists;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.encoders.Hex;
import solar.network.Schnorr.KeyUtil;
import solar.network.Schnorr.Schnorr;
import solar.network.bean.*;
import solar.network.enums.TransactionHeaderType;
import solar.network.utils.HexUtil;
import solar.network.utils.KeyUtils;
import solar.network.utils.Point;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import static org.bitcoinj.core.Base58.encode;


public class Main {

    static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
    static final ECDomainParameters CURVE = new ECDomainParameters(
            CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
    static final BigInteger HALF_CURVE_ORDER = CURVE_PARAMS.getN().shiftRight(1);

    public static void main(String[] args) throws Exception {
        String privateKey = "4f255c0ac45ee549c207fd0b34ad4332d1d612e143c2154c5a811a5bbca017a1";
        byte[] priKeyBytes = Hex.decode(privateKey);
        ECPrivateKeyParameters privKeys = new ECPrivateKeyParameters(new BigInteger(1, priKeyBytes), CURVE);
        //signer.init(true, privKey);

        byte[] privateKeyBytes = privKeys.getD().toByteArray();


        ECPoint q = new FixedPointCombMultiplier().multiply(CURVE.getG(), new BigInteger(1, privateKeyBytes));
        ECPublicKeyParameters p = new ECPublicKeyParameters(q, CURVE);

        byte[] privateKeyWeekBytes = KeyUtil.getTweakedPrivKey(privateKeyBytes);

        byte[] publicWeekBytes =  KeyUtil.getTweakedPubKey(privateKeyBytes);

        byte[] pubBytes = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(p).getEncoded(ASN1Encoding.DER);
        pubBytes = KeyUtils.normalizePublicKey(pubBytes);
        System.out.println("publicKey:"+Hex.toHexString(pubBytes));

        byte version = (byte) 30;;
        //byte version = (byte) 63;

        byte[] payload = Utils.sha256hash160(pubBytes);

        byte[] addressBytes = new byte[1 + payload.length + 4];
        addressBytes[0] = version;

        System.arraycopy(payload, 0, addressBytes, 1, payload.length);
        byte[] checksum = Sha256Hash.hashTwice(addressBytes, 0, payload.length + 1);
        System.arraycopy(checksum, 0, addressBytes, payload.length + 1, 4);
        String sender = encode(addressBytes);
        System.out.println(sender);
        TransferTransaction transferTransaction = new TransferTransaction();
        transferTransaction.setSenderPublicKey(Hex.toHexString(pubBytes));
        transferTransaction.setHeaderType(TransactionHeaderType.Standard.getValue());
        transferTransaction.setType(6);
        transferTransaction.setTypeGroup(1);

        TransferAsset transferAsset = new TransferAsset();
        transferAsset.setRecipientId("DGceDC1xZPBCuJpNY4DDVcRCxqcGAYuKyQ");
        transferAsset.setAmount("110000000");
        Transfers transfers = new Transfers();
        transfers.setTransfers(Lists.newArrayList(transferAsset));
        transferTransaction.setAsset(transfers);
        transferTransaction.setSenderId(sender);
        transferTransaction.setNonce(BigInteger.ONE);
        //transferTransaction.setNonce(new BigInteger("123178231"));
        //testnet
        transferTransaction.setNetwork(30);
        //mainnet
        //transactionData.setNetwork(63);
        transferTransaction.setVersion(3);
        transferTransaction.setMemo("Poloniex Wallet");
        transferTransaction.setFee(new BigInteger("4000000"));

        SerialiseOptions serialiseOptions = new SerialiseOptions();
        serialiseOptions.setDisableVersionCheck(true);

        byte[] serilizeBytes = Serialiser.getBytes(transferTransaction,serialiseOptions);
        transferTransaction.setSerialised(serilizeBytes);
        byte[] result = Sha256Hash.hash(serilizeBytes);

        transferTransaction.setId(Hex.toHexString(result));

        byte[] sig = Schnorr.sign(result,privateKeyWeekBytes,new SecureRandom().generateSeed(32));

        String signure = Hex.toHexString(sig);

        transferTransaction.setSignature(signure);
        serialiseOptions.setExcludeSignature(false);


        boolean verify = Schnorr.verify(result,publicWeekBytes,sig);
        System.out.println(verify);

        Transactions transactions = new Transactions();

        byte[] id = Serialiser.getBytes(transferTransaction,serialiseOptions);
        transactions.setId(Hex.toHexString(Sha256Hash.hash(id)));
        transactions.setAsset(transfers);
        transactions.setNonce(transferTransaction.getNonce());
        transactions.setSenderPublicKey(transferTransaction.getSenderPublicKey());
        transactions.setFee(transferTransaction.getFee());
        transactions.setVersion(3);
        transactions.setMemo(transferTransaction.getMemo());
        transactions.setType(6);
        transactions.setTypeGroup(1);
        transactions.setSignature(transferTransaction.getSignature());

        System.out.println(transactions);

    }


    public static byte[] sign(byte[] msg, byte[] secKey) throws Exception    {
        if(msg.length != 32)    {
            throw new Exception("The message must be a 32-byte array.");
        }
        BigInteger secKey0 = HexUtil.bigIntFromBytes(secKey);

        if(!(BigInteger.ONE.compareTo(secKey0) <= 0 && secKey0.compareTo(Point.getn().subtract(BigInteger.ONE)) <= 0)) {
            throw new Exception("The secret key must be an integer in the range 1..n-1.");
        }
        Point P = Point.mul(Point.getG(), secKey0);
        if(!P.hasSquareY())    {
            secKey0 = Point.getn().subtract(secKey0);
        }
        int len = HexUtil.bytesFromBigInteger(secKey0).length + msg.length;
        byte[] buf = new byte[len];
        System.arraycopy(HexUtil.bytesFromBigInteger(secKey0), 0, buf, 0, HexUtil.bytesFromBigInteger(secKey0).length);
        System.arraycopy(msg, 0, buf, HexUtil.bytesFromBigInteger(secKey0).length, msg.length);
        BigInteger k0 = HexUtil.bigIntFromBytes(Point.taggedHash("BIPSchnorrDerive", buf)).mod(Point.getn());
        if(k0.compareTo(BigInteger.ZERO) == 0)    {
            throw new Exception("Failure. This happens only with negligible probability.");
        }
        Point R = Point.mul(Point.getG(), k0);
        BigInteger k = null;
        if(!R.hasSquareY())    {
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
        BigInteger e = HexUtil.bigIntFromBytes(Point.taggedHash("BIPSchnorr", buf)).mod(Point.getn());
        BigInteger kes = k.add(e.multiply(secKey0)).mod(Point.getn());
        len = R.toBytes().length + HexUtil.bytesFromBigInteger(kes).length;
        byte[] ret = new byte[len];
        System.arraycopy(R.toBytes(), 0, ret, 0, R.toBytes().length);
        System.arraycopy(HexUtil.bytesFromBigInteger(kes), 0, ret, R.toBytes().length, HexUtil.bytesFromBigInteger(kes).length);
        return ret;
    }

    public static boolean verify(byte[] msg, byte[] pubkey, byte[] sig) throws Exception    {
        if(msg.length != 32)    {
            throw new Exception("The message must be a 32-byte array.");
        }
        if(pubkey.length == 33){
            byte[] pubkeyBytes = new byte[32];
            System.arraycopy(pubkey,1,pubkeyBytes,0,32);
            pubkey = pubkeyBytes;
        }

        if(pubkey.length != 32)    {
            throw new Exception("The public key must be a 32-byte array.");
        }
        if(sig.length != 64)    {
            throw new Exception("The signature must be a 64-byte array.");
        }

        Point P = Point.pointFromBytes(pubkey);
        if(P == null)    {
            return false;
        }
        BigInteger r = HexUtil.bigIntFromBytes(Arrays.copyOfRange(sig,0, 32));
        BigInteger s = HexUtil.bigIntFromBytes(Arrays.copyOfRange(sig,32, 64));
        if(r.compareTo(Point.getp()) >= 0 || s.compareTo(Point.getn()) >= 0)    {
            return false;
        }
        int len = 32 + pubkey.length + msg.length;
        byte[] buf = new byte[len];
        System.arraycopy(sig, 0, buf, 0, 32);
        System.arraycopy(pubkey, 0, buf, 32, pubkey.length);
        System.arraycopy(msg, 0, buf, 32 + pubkey.length, msg.length);
        BigInteger e = HexUtil.bigIntFromBytes(Point.taggedHash("BIPSchnorr", buf)).mod(Point.getn());
        Point R = Point.add(Point.mul(Point.getG(), s), Point.mul(P, Point.getn().subtract(e)));
        if(R == null || !R.hasSquareY() || R.getX().compareTo(r) != 0)    {
            return false;
        }
        else    {
            return true;
        }
    }
}
