package model;

public class Task {
    private  int rest; //剩余生命周期
    private int k;  //最大执行次数上限
    private double wl; //工作负载
    private double ip; //输入数据量
    private double op; //输出数据量
    private double wait;//任务在队列中的等待时间

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

    public double getWait() {
        return wait;
    }

    public void setWait(double wait) {
        this.wait = wait;
    }

    public Task(int rest, int k, double wl, double ip, double op, double wait) {
        this.rest = rest;
        this.k = k;
        this.wl = wl;
        this.ip = ip;
        this.op = op;
        this.wait = wait;
    }
}
