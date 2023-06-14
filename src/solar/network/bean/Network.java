package solar.network.bean;

import lombok.Data;

import java.util.Map;

@Data
public class Network {
    private String name;
    private String messagePrefix;
    private Map<String,String> bip32;
    private Integer pubKeyHash;
    private String nethash;
    private Integer wif;
    private Integer slip44;
    private Map<String,String> client;
}
