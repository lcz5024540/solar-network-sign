package solar.network.bean;

import lombok.Data;

import java.util.List;

@Data
public class Exceptions {

    private List<String> blocks;
    private List<String> transactions;
}
