package solar.network.utils;

import org.bitcoinj.core.ECKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;

import java.io.IOException;

public class KeyUtils {
    public KeyUtils() {
    }

    public static ECKey toBlockchainUtxoKey(ECPublicKeyParameters publicKeyParameters) {
        return ECKey.fromPublicOnly(publicKeyParameters.getQ().getEncoded(true));
    }

    public static ECPublicKeyParameters decodePublicKey(byte[] encoded) throws IOException {
        AsymmetricKeyParameter pub = PublicKeyFactory.createKey(encoded);
        return (ECPublicKeyParameters)pub;
    }

    public static byte[] normalizePublicKey(byte[] publicKey) throws IOException {
        if (!needDecode(publicKey)) {
            return publicKey;
        } else {
            ECPublicKeyParameters ecPub = decodePublicKey(publicKey);
            return ECKey.fromPublicOnly(ecPub.getQ().getEncoded(true)).getPubKey();
        }
    }

    private static boolean needDecode(byte[] pubKeyBytes) {
        return pubKeyBytes.length > 65;
    }

    public static boolean isCompressed(int length) {
        return length < 65;
    }
}
