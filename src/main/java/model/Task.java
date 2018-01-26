package model;

public class Task {
    private  int rest; //剩余声明周期
    private int k;  //最大执行次数上限
    private double wl; //剩余生命周期
    private double ip; //输入数据量
    private double op; //输出数据量

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public double getWl() {
        return wl;
    }

    public void setWl(double wl) {
        this.wl = wl;
    }

    public double getIp() {
        return ip;
    }

    public void setIp(double ip) {
        this.ip = ip;
    }

    public double getOp() {
        return op;
    }

    public void setOp(double op) {
        this.op = op;
    }
}
