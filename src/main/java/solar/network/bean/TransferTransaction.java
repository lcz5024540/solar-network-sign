package solar.network.bean;


import lombok.Data;
import solar.network.Schnorr.Util;
import solar.network.enums.TransactionTypeGroup;
import solar.network.constants.TransactionType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class TransferTransaction extends Transaction {
    public static int typeGroup = TransactionTypeGroup.Core.getValue();
    public static int type = TransactionType.Transfer.getValue();
    public static String key = "transfer";

    protected static BigInteger defaultStaticFee = BigInteger.valueOf(10000000L);

    public DeserialiseAddresses getAddresses() {
        DeserialiseAddresses addresses = super.getAddresses();
        List<TransferAsset> transferList = this.getAsset().getTransfers();
        List<String> recipientId = transferList.stream().map(TransferAsset::getRecipientId).collect(Collectors.toList());
        addresses.setRecipientId(recipientId.toArray(new String[recipientId.size()]));
        return addresses;
    }

    public Transfer getSchema() {
        return new Transfer();
    }

    public ByteBuffer serialise(SerialiseOptions options) {
        if ( this.getAsset() != null  &&  this.getAsset().getTransfers().size()!=0 ) {
            ByteBuffer buff = ByteBuffer.allocate(2 + this.getAsset().getTransfers().size() * 29);

            byte[] lengthBytes = new byte[2];
            int length = this.getAsset().getTransfers().size();
            lengthBytes[0] = (byte)length;
            buff.put(lengthBytes);
            //buff.putInt(this.getAsset().getTransfers().size());

            for(TransferAsset transferAsset : this.getAsset().getTransfers()){
                BigInteger amount = new BigDecimal(transferAsset.getAmount()).toBigInteger();
                buff.put(Util.bigInteger2Bytes(amount));
                //BigInteger amount = new BigDecimal(transferAsset.getAmount()).toBigInteger();
                //buff.put(amount.toByteArray(),buff.arrayOffset(),amount.toByteArray().length);

                Map<String,Object> addressBufferResult = Address.toBuffer(transferAsset.getRecipientId());
                if (addressBufferResult.get("addressError") != null) {
                    throw new RuntimeException(addressBufferResult.get("addressError").toString());
                }

                buff.put(Hex.decode(addressBufferResult.get("addressBuffer").toString()));

            }


            return buff;
        }
        return null;
    }
}
