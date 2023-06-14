package solar.network.bean;

import lombok.Data;

@Data
public class DeserialiseAddresses {
    private String senderId;
    private String[] recipientId;
}
