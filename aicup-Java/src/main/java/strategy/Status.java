package strategy;

public class Status {
    private Integer builderId; // builder, who NOT extract resources
    private Boolean building = false; // builderId builds

    public Boolean isBuilding() {
        return building;
    }

    public void setBuilding(Boolean building) {
        this.building = building;
    }

    public Integer getBuilderId() {
        return builderId;
    }

    public void setBuilderId(Integer builderId) {
        this.builderId = builderId;
    }
}
