package reward;

/**
 * 策略执行的返回结果
 */
public class RewardBackValue {
    private double failureRate; // 失败率
    private double rTT;  //最长执行时间
    private double cost; // 执行代价

    public RewardBackValue(double failureRate, double rTT, double cost) {
        this.failureRate = failureRate;
        this.rTT = rTT;
        this.cost = cost;
    }

    public double getFailureRate() {
        return failureRate;
    }

    public void setFailureRate(double failureRate) {
        this.failureRate = failureRate;
    }

    public double getrTT() {
        return rTT;
    }

    public void setrTT(double rTT) {
        this.rTT = rTT;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
