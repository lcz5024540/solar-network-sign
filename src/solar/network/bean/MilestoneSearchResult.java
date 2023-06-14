package solar.network.bean;

public class MilestoneSearchResult {
    private boolean found;
    private Integer height;
    private Object data;

    public MilestoneSearchResult(boolean found, Integer height, Object data) {
        this.found = found;
        this.height = height;
        this.data = data;
    }

    public boolean isFound() {
        return found;
    }

    public Integer getHeight() {
        return height;
    }

    public Object getData() {
        return data;
    }
}
