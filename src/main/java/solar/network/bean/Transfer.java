package solar.network.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Transfer {

    private String id = "transfer";

    //private List<String> required = new ArrayList<String>(Arrays.asList("recipientId", "amount"));
    private Map<String,Object> required = new HashMap<>();

    private Map<String,Object> properties;
}
