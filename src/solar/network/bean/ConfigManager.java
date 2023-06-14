package solar.network.bean;


import solar.network.exceptions.InvalidMilestoneConfigurationError;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private Map<String, Object> config;
    private Integer height;
    private Map<String, Object> milestone;
    private Map<String, Object>[] milestones;

    private Map<String, NetworkConfig> networks;

    public ConfigManager() {
        try {
            Networks networkMap = new Networks();
            networks = networkMap.getNetworks();
            //this.setConfig(networks.get("mainnet"));
            this.setConfig(networks.get("testnet"));
        } catch (InvalidMilestoneConfigurationError e) {
            throw new RuntimeException(e);
        }
    }

    public void setConfig(NetworkConfig config) throws InvalidMilestoneConfigurationError {
        this.config = new HashMap<>();
        this.config.put("network", config.getNetwork());
        this.config.put("exceptions", config.getExceptions());
        this.config.put("milestones", config.getMilestones());
        //this.config.put("genesisBlock", config.getGenesisBlock());
        //this.validateMilestones();
        //this.buildConstants();
    }

    public void setFromPreset(String network) {
        try {
            this.setConfig(this.getPreset(network));
        } catch (InvalidMilestoneConfigurationError e) {
            throw new RuntimeException(e);
        }
    }

    public NetworkConfig getPreset(String network) {
        return networks.get(network.toLowerCase());
    }

    public Map<String, Object> all() {
        return this.config;
    }

    public <T> void set(String key, T value) {
        if (this.config == null) {
            throw new Error();
        }
        this.config.put(key, value);
    }

    public <T> T get(String key) {
        return (T) this.config.get(key);
    }

    public void setHeight(Integer value) {
        this.height = value;
    }

    public Integer getHeight() {
        return this.height;
    }

    public boolean isNewMilestone(Integer height) {
        height = height != null ? height : this.height;
        if (this.milestones == null) {
            throw new Error();
        }
        for (Map<String, Object> milestone : this.milestones) {
            if (milestone.get("height").equals(height)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Object> getMilestone(Integer height) {
        if (this.milestone == null || this.milestones == null) {
            throw new Error();
        }
        if (height == null && this.height != null) {
            height = this.height;
        }
        if (height == null) {
            height = 1;
        }
        while (Integer.parseInt(this.milestone.get("index").toString()) < this.milestones.length - 1 &&
                height >= (Integer) this.milestones[ Integer.parseInt(this.milestone.get("index").toString()) + 1].get("height")) {
            this.milestone.put("index", (Integer) this.milestone.get("index") + 1);
            this.milestone.put("data", this.milestones[(Integer) this.milestone.get("index")]);
        }
        while (height < (Integer) this.milestones[(Integer) this.milestone.get("index")].get("height")) {
            this.milestone.put("index", (Integer) this.milestone.get("index") - 1);
            this.milestone.put("data", this.milestones[(Integer) this.milestone.get("index")]);
        }
        return this.milestone;
    }

    public MilestoneSearchResult getNextMilestoneWithNewKey(Integer previousMilestone, String key) {
        if (this.milestones == null || this.milestones.length == 0) {
            throw new Error("Attempted to get next milestone but none were set");
        }
        for (Map<String, Object> milestone : this.milestones) {
            if (milestone.get(key) != null &&
                    !milestone.get(key).equals(this.getMilestone(previousMilestone).get(key)) &&
                    (Integer) milestone.get("height") > previousMilestone) {
                return new MilestoneSearchResult(true, (Integer) milestone.get("height"), milestone.get(key));
            }
        }
        return new MilestoneSearchResult(false, previousMilestone, null);
    }

    public Map<String, Object>[] getMilestones() {
        return this.milestones;
    }

    private void buildConstants() {
        if (this.config == null) {
            throw new Error();
        }
        this.milestones = (Map<String, Object>[]) this.config.get("milestones");
        this.milestones = sortMilestones(this.milestones);
        this.milestone = new HashMap<>();
        this.milestone.put("index", 0);
        this.milestone.put("data", this.milestones[0]);
        int lastMerged = 0;
        while (lastMerged < this.milestones.length - 1) {
            this.milestones[lastMerged + 1] = deepmerge(this.milestones[lastMerged], this.milestones[lastMerged + 1]);
            lastMerged++;
        }
    }

    private void validateMilestones() throws InvalidMilestoneConfigurationError {
        if (this.config == null) {
            throw new Error();
        }
        Map<String, Object>[] delegateMilestones = sortMilestones((Map<String, Object>[]) this.config.get("milestones"));
        for (int i = 1; i < delegateMilestones.length; i++) {
            Map<String, Object> previous = delegateMilestones[i - 1];
            Map<String, Object> current = delegateMilestones[i];
            if (previous.get("activeDelegates").equals(current.get("activeDelegates"))) {
                continue;
            }
            if (((Integer) current.get("height") - (Integer) previous.get("height")) % (Integer) previous.get("activeDelegates") != 0) {
                throw new InvalidMilestoneConfigurationError("Bad milestone at height: " + current.get("height") + ". The number of delegates can only be changed at the beginning of a new round");
            }
        }
    }

    private Map<String, Object>[] sortMilestones(Map<String, Object>[] milestones) {
        return Arrays.stream(milestones)
                .sorted((a, b) -> (Integer) a.get("height") - (Integer) b.get("height"))
                .toArray(Map[]::new);
    }

    private Map<String, Object> deepmerge(Map<String, Object> a, Map<String, Object> b) {
        Map<String, Object> result = new HashMap<>(a);
        for (Map.Entry<String, Object> entry : b.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (result.containsKey(key) && result.get(key) instanceof Map && value instanceof Map) {
                result.put(key, deepmerge((Map<String, Object>) result.get(key), (Map<String, Object>) value));
            } else {
                result.put(key, value);
            }
        }
        return result;
    }
}

