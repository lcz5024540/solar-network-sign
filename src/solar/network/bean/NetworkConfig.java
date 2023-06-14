package solar.network.bean;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NetworkConfig {

    private Exceptions exceptions;

    private BlockJson genesisBlock;

    private List<Map<String, Object>> milestones;

    private Network network;
}
