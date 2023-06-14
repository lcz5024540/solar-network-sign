package solar.network.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

@Data
public abstract class Transaction {

    private String id;

    private Integer typeGroup;

    private Integer type;

    private Boolean verified;

    private String key;

    private BigDecimal staticFee;

    private Boolean isVerified;

    private DeserialiseAddresses addresses;

    private TransactionData data;

    private ByteBuffer serialised;

    private Long timestamp;

//    private TransactionJson toJson;

//    void setBurnedFee(int height);

    public abstract ByteBuffer serialise(SerialiseOptions options);
//    void deserialise(ByteBuffer buf, IDeserialiseAddresses transactionAddresses);
//
//    Boolean verify(IVerifyOptions options);
//    ISchemaValidationResult verifySchema(Boolean strict);
//
//    ITransactionJson toJson();
}
