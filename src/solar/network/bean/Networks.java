package solar.network.bean;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Data
public class Networks {

    public Map<String, NetworkConfig> networks;

    public Networks(){
        networks = new HashMap<>();
        NetworkConfig miannet = this.getJson("mainnet");
        NetworkConfig testnet = this.getJson("testnet");
        networks.put("mainnet",miannet);
        networks.put("testnet",testnet);
    }

    public NetworkConfig getJson(String netWorkName){
        NetworkConfig networkConfig = new NetworkConfig();
        JSONArray arry = null;
        try {
            URL networkUrl = this.getClass().getClassLoader().getResource("network/"+netWorkName+"/network.json");
            String network = FileUtils.readFileToString(new File(networkUrl.getPath()), StandardCharsets.UTF_8);
            networkConfig.setNetwork(JSON.parseObject(network, Network.class));

            URL exceptionUrl = this.getClass().getClassLoader().getResource("network/"+netWorkName+"/exceptions.json");
            String exception = FileUtils.readFileToString(new File(exceptionUrl.getPath()), StandardCharsets.UTF_8);
            networkConfig.setExceptions(JSON.parseObject(exception, Exceptions.class));

            URL genesisBlockUrl = this.getClass().getClassLoader().getResource("network/"+netWorkName+"/genesisBlock.json");
            String block = FileUtils.readFileToString(new File(genesisBlockUrl.getPath()), StandardCharsets.UTF_8);
            networkConfig.setGenesisBlock(JSON.parseObject(block, BlockJson.class));

//            URL genesisBlockUrl = this.getClass().getClassLoader().getResource("network/"+netWorkName+"/genesisBlock.json");
//            String block = FileUtils.readFileToString(new File(genesisBlockUrl.getPath()), StandardCharsets.UTF_8);
//            networkConfig.setMilestones(JSON.parseObject(block, BlockJson.class));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return networkConfig;
    }



    public static void main(String[] args) {
//        Networks networks1 = new Networks();
//        String json = networks1.getJson("mainnet");
//        Network network = JSON.parseObject(json, Network.class);
//        System.out.println(network);

        Networks networks1 = new Networks();
        Map<String,NetworkConfig> networkConfigMap = networks1.networks;
        NetworkConfig mainnet = networkConfigMap.get("mainnet");
        System.out.println(mainnet);
    }
 }
