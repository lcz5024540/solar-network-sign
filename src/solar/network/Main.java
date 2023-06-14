package solar.network;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.encoders.Hex;
import solar.network.bean.LegacyTransferTransaction;
import solar.network.bean.SerialiseOptions;
import solar.network.bean.Serialiser;
import solar.network.bean.TransactionData;
import solar.network.enums.TransactionHeaderType;
import solar.network.utils.KeyUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import static org.bitcoinj.core.Base58.encode;

public class Main {

    static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
    static final ECDomainParameters CURVE = new ECDomainParameters(
            CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
    static final BigInteger HALF_CURVE_ORDER = CURVE_PARAMS.getN().shiftRight(1);

    public static void main(String[] args) throws Exception {
        ECDSASigner signer;
        signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
        String privateKey = "4f255c0ac45ee549c207fd0b34ad4332d1d612e143c2154c5a811a5bbca017a1";
        byte[] privateKeyBytes = Hex.decode(privateKey);
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(new BigInteger(1, privateKeyBytes), CURVE);
        signer.init(true, privKey);

        ECPoint q = new FixedPointCombMultiplier().multiply(CURVE.getG(), new BigInteger(1, privateKeyBytes));
        ECPublicKeyParameters p = new ECPublicKeyParameters(q, CURVE);
        byte[] pubBytes;
        try {
            pubBytes = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(p).getEncoded(ASN1Encoding.DER);
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
        System.out.println("publicKey:"+Hex.toHexString(pubBytes));

        byte version = (byte) 30;;
        //byte version = (byte) 30;

        byte[] payload = Utils.sha256hash160(pubBytes);

        byte[] addressBytes = new byte[1 + payload.length + 4];
        addressBytes[0] = version;

        System.arraycopy(payload, 0, addressBytes, 1, payload.length);
        byte[] checksum = Sha256Hash.hashTwice(addressBytes, 0, payload.length + 1);
        System.arraycopy(checksum, 0, addressBytes, payload.length + 1, 4);
        String sender = encode(addressBytes);
        System.out.println(sender);
        LegacyTransferTransaction legacyTransferTransaction = new LegacyTransferTransaction();
        TransactionData transactionData = new TransactionData();
        transactionData.setSenderPublicKey(Hex.toHexString(KeyUtils.normalizePublicKey(pubBytes)));
        transactionData.setHeaderType(TransactionHeaderType.Standard.getValue());
        legacyTransferTransaction.setType(6);
        legacyTransferTransaction.setTypeGroup(1);
        transactionData.setAmount(new BigInteger("110000000"));
        transactionData.setSenderId(sender);
        transactionData.setNonce(BigInteger.ZERO);
        //testnet
        transactionData.setNetwork(30);
        //mainnet
        //transactionData.setNetwork(63);
        transactionData.setVersion(3);
        transactionData.setMemo("Poloniex Wallet");
        transactionData.setFee(new BigInteger("4000000"));
        transactionData.setRecipientId("DGceDC1xZPBCuJpNY4DDVcRCxqcGAYuKyQ");
        legacyTransferTransaction.setData(transactionData);
        SerialiseOptions serialiseOptions = new SerialiseOptions();
        serialiseOptions.setDisableVersionCheck(true);
        byte[] serilizeBytes = Serialiser.getBytes(legacyTransferTransaction,serialiseOptions);
        byte[] result = Sha256Hash.hash(serilizeBytes);

        BigInteger[] components = signer.generateSignature(result);

        ECKey.ECDSASignature ecdsaSig = new ECKey.ECDSASignature(components[0], components[1]).toCanonicalised();

        ByteArrayOutputStream baos = new ByteArrayOutputStream(72);
        try {
            DERSequenceGenerator der = new DERSequenceGenerator(baos);
            der.addObject(new ASN1Integer(ecdsaSig.r));
            der.addObject(new ASN1Integer(ecdsaSig.s));
            der.close();
            byte[] sig = baos.toByteArray();

            String signuture = Hex.toHexString(sig);
            System.out.println(signuture);
        } catch (IOException e) {
            // TODO: log
        }

    }
}
