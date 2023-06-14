package solar.network.bean;



import org.apache.commons.lang3.StringUtils;
import solar.network.constants.TransactionType;
import solar.network.enums.TransactionTypeGroup;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Map;

public class LegacyTransferTransaction extends Transaction {
    public static int typeGroup = TransactionTypeGroup.Core.getValue();
    public static int type = TransactionType.LegacyTransfer.getValue();
    public static String key = "legacyTransfer";

    protected static BigInteger defaultStaticFee = BigInteger.valueOf(10000000L);

    public DeserialiseAddresses getAddresses() {
        DeserialiseAddresses addresses = super.getAddresses();
        addresses.setRecipientId(new String[] { this.getData().getRecipientId() });
        return addresses;
    }

    public LegacyTransfer getSchema() {
        return new LegacyTransfer();
    }

    public ByteBuffer serialise(SerialiseOptions options) {
        ByteBuffer buff = ByteBuffer.allocate(33);
        buff.putLong(this.getData().getAmount().longValue());
        buff.putInt(this.getData().getExpiration() != null ? this.getData().getExpiration() : 0);

        if (StringUtils.isNotEmpty(this.getData().getRecipientId())) {
            Map<String,Object> addressBufferResult = Address.toBuffer(this.getData().getRecipientId());
            if (addressBufferResult.get("addressError") != null) {
                throw new RuntimeException(addressBufferResult.get("addressError").toString());
            }

            buff.put(addressBufferResult.get("addressBuffer").toString().getBytes());
        }

        return buff;
    }
}
