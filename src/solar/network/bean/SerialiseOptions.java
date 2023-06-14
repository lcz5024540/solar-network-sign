package solar.network.bean;

import lombok.Data;

@Data
public class SerialiseOptions {
    private Boolean acceptLegacyVersion;
    private Boolean disableVersionCheck;
    private Boolean excludeSignature;
    private Boolean excludeSecondSignature;
    private Boolean excludeMultiSignature;
    private String addressError;
}
