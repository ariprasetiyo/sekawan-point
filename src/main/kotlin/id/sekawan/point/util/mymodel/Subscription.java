package id.sekawan.point.util.mymodel;

public class Subscription {
    private String id;
    private String name;
    private Long price;
    private String priceDescription;
    private Long penaltyFee;
    private Long serviceFee;
    private int durationDays;
    private Boolean hasMaxCycleCount;
    private int maxCycleCount;
    private int holderHeadOfficeId;
    private String loginUser;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getPriceDescription() {
        return priceDescription;
    }

    public void setPriceDescription(String priceDescription) {
        this.priceDescription = priceDescription;
    }

    public Long getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(Long penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public Long getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Long serviceFee) {
        this.serviceFee = serviceFee;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public Boolean getHasMaxCycleCount() {
        return hasMaxCycleCount;
    }

    public void setHasMaxCycleCount(Boolean hasMaxCycleCount) {
        this.hasMaxCycleCount = hasMaxCycleCount;
    }

    public int getMaxCycleCount() {
        return maxCycleCount;
    }

    public void setMaxCycleCount(int maxCycleCount) {
        this.maxCycleCount = maxCycleCount;
    }

    public int getHolderHeadOfficeId() {
        return holderHeadOfficeId;
    }

    public void setHolderHeadOfficeId(int holderHeadOfficeId) {
        this.holderHeadOfficeId = holderHeadOfficeId;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }
}