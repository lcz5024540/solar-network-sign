package solar.network.bean;


import lombok.Data;
import solar.network.constants.TransactionType;
import solar.network.enums.TransactionTypeGroup;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Map;

@Data
public class LegacyTransferTransaction extends Transaction {
    public static int typeGroup = TransactionTypeGroup.Core.getValue();
    public static int type = TransactionType.LegacyTransfer.getValue();
    public static String key = "legacyTransfer";

    private String recipientId;

    private Integer expiration;

    private BigInteger amount;


    protected static BigInteger defaultStaticFee = BigInteger.valueOf(10000000L);

    public DeserialiseAddresses getAddresses() {
        DeserialiseAddresses addresses = super.getAddresses();
        addresses.setRecipientId(new String[] { this.getRecipientId() });
        return addresses;
    }

    public LegacyTransfer getSchema() {
        return new LegacyTransfer();
    }

    public ByteBuffer serialise(SerialiseOptions options) {
        ByteBuffer buff = ByteBuffer.allocate(33);
        buff.putLong(this.getAmount().longValue());
        buff.putInt(this.getExpiration() != null ? this.getExpiration() : 0);

        if (StringUtils.isNotEmpty(this.getRecipientId())) {
            Map<String,Object> addressBufferResult = Address.toBuffer(this.getRecipientId());
            if (addressBufferResult.get("addressError") != null) {
                throw new RuntimeException(addressBufferResult.get("addressError").toString());
            }

            buff.put(addressBufferResult.get("addressBuffer").toString().getBytes());
        }

        return buff;
    }
}
