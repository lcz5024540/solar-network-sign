package solar.network.bean;

import solar.network.Schnorr.Util;
import solar.network.enums.TransactionHeaderType;
import solar.network.enums.TransactionTypeGroup;
import org.bouncycastle.util.encoders.Hex;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Serialiser {

    public static byte[] getBytes(Transaction transaction, SerialiseOptions options) throws Exception {
        return serialise(transaction,options);
    }

    public static byte[] serialise(Transaction transaction, SerialiseOptions options) throws Exception {
        int size = 83886;
        //ConfigManager configManager = new ConfigManager();

        ByteBuffer buff = ByteBuffer.allocate(size);

        serialiseCommon(transaction, buff);
        serialiseMemo(transaction, buff);

        ByteBuffer serialised = transaction.serialise(options);

        if (serialised == null) {
            throw new Exception();
        }

        buff.put(serialised.array());

        serialiseSignatures(transaction, buff, options);

        buff.mark();
        int position = buff.position();
        byte[] bytesResult = new byte[position];
        for(int i=0;i< position;i++){
            bytesResult[i] = buff.get(i);
        }
        return bytesResult;
    }

    private static ByteBuffer serialiseCommon(Transaction transaction, ByteBuffer buff) {
        int version = transaction.getVersion() != null ? transaction.getVersion() : 0x03;
        int headerType = transaction.getHeaderType() != null ? transaction.getHeaderType() : 0x00;

        if (transaction.getTypeGroup() == null) {
            transaction.setTypeGroup(TransactionTypeGroup.Core.getValue());
        }
        buff.put((byte) (0xff - headerType));
        buff.put((byte) version);
        ConfigManager configManager = new ConfigManager();
        buff.put((byte) (transaction.getNetwork() != null ? transaction.getNetwork() : Integer.parseInt(((Network)configManager.get("network")).getPubKeyHash().toString())));

        byte[] typeGroupBytes = new byte[4];
        typeGroupBytes[0] = transaction.getTypeGroup().byteValue();
        buff.put(typeGroupBytes);
        byte[] typeBytes = new byte[2];
        typeBytes[0] = transaction.getType().byteValue();
        buff.put(typeBytes);
        byte[] nonceBytes = new byte[8];
        byte[] nonce = transaction.getNonce().toByteArray();
        for(int i=0;i<nonce.length;i++){
            nonceBytes[i] = nonce[i];
        }
        buff.put(nonceBytes);

        buff.put(Hex.decode(transaction.getSenderPublicKey()));
        if (transaction.getHeaderType() == TransactionHeaderType.Extended.getValue()) {
            Map<String,Object> addressBufferResult = Address.toBuffer(transaction.getSenderId());
            if (addressBufferResult.get("addressError") != null) {
                throw new RuntimeException(addressBufferResult.get("addressError").toString());
            }

            buff.put(Hex.decode(addressBufferResult.get("addressBuffer").toString()));
        }
        buff.put(Util.bigInteger2Bytes(transaction.getFee()));
        return buff;
    }

    private static void serialiseMemo(Transaction transaction, ByteBuffer buff) {
        if (transaction.getMemo() != null) {
            byte[] memo = transaction.getMemo().getBytes(StandardCharsets.UTF_8);
            buff.put((byte) memo.length);
            buff.put(memo);
        } else {
            buff.put((byte) 0x00);
        }
    }

    private static void serialiseSignatures(Transaction transaction, ByteBuffer buff, SerialiseOptions options) {
        if (transaction.getSignature() != null && !options.getExcludeSignature()) {
            buff.put(Hex.decode(transaction.getSignature()));
        }

        String secondSignature = transaction.getSecondSignature() != null ? transaction.getSecondSignature() : transaction.getSignSignature();

        if (secondSignature != null && !options.getExcludeSecondSignature()) {
            buff.put(Hex.decode(secondSignature));
        }

        if (transaction.getSignatures() != null) {
            if (!options.getExcludeMultiSignature()) {
                String signatures = String.join("", transaction.getSignature());
                buff.put(Hex.decode(signatures));
            }
        }
    }
}