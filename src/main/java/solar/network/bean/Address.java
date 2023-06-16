package solar.network.bean;

import org.bitcoinj.core.Base58;

import java.util.HashMap;
import java.util.Map;

public class Address {

    public static Map<String, Object> toBuffer(String address) {
        byte[] buffer = Base58.decodeChecked(address);
        ConfigManager configManager = new ConfigManager();
        Integer networkVersion = Integer.valueOf(((Network)configManager.get("network")).getPubKeyHash().toString());
        Map<String, Object> result = new HashMap<>();
        result.put("addressBuffer", Hex.toHexString(buffer));

        if (buffer[0] != networkVersion.byteValue()) {
            result.put("addressError", String.format("Expected address network byte %d, but got %d.", networkVersion, buffer[0]));
        }
        return result;
    }
}
