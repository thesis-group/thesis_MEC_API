package property;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 变量表
 */
public class VariableTable {

    //zone ids, z is the maximum id value
    public static double z = 0;
    //the number of nodes contacting users
    public static double v = 0;
    //the length of queue, q is the maximum length of queue
    public static double q = 0;
    //the number of users connected to access point in the current zone
    public static double n = 0;
    //the length of decision period
    public static double tao = 0;
    //The probability that the user wonder from zone i to zone j.
    public static double Pij = 0;
    //The input data size of 𝑡𝑖
    public static double ip = 0;
    //The output data size of 𝑡𝑖
    public static double op = 0;
    //The workload of 𝑡𝑖
    public static double wl = 0;
    //The arrival time of 𝑡𝑖
    public static double ar = 0;
    //The unit execution energy consumption
    public static double eCpu = 0;
    //The processing speed of local execution
    public static double sCpu = 0;
    //Unit payment of local execution
    public static double cCpu = 0;
    //Unit payment of cloudlet execution
    public static double cCloudlet = 0;
    //Processing speed of cloudlet
    public static double sCloudlet = 0;
    //the unit power consumption of uploading input data of task 𝑡𝑖
    public static double eUp = 0;
    //the unit energy consumption of downloading output data of task 𝑡𝑖
    public static double eDown = 0;
    //the total uplink bandwidth of access point
    public static double rUp = 0;
    //the total downlink bandwidth of access point
    public static double rDown = 0;
    //Processing speed of k type nodes
    public static List<Double> sAdK;
    //transmission rate of ad hoc network
    public static double rAd = 0;
    //The unit energy consumption of data transmission
    public static double eAd = 0;
    //The payment for one copy in ad hoc network
    public static double cAd = 0;
    //Local processing failure rate
    public static double fl = 0;
    //Uplink failure rate
    public static double fUp = 0;
    //downlink failure rate
    public static double fDown = 0;
    //Transmission failure rate
    public static double ft = 0;
    //Ad hoc network processing failure rate
    public static double fAd = 0;
    //Unit failure penalty
    public static double cPen = 0;

    //用户通信半径
    public static double r = 0;
    //服务节点通信半径
    public static double R = 0;
    //用户移动速度
    public static double v1 = 0;
    //服务节点移动速度
    public static double v2 = 0;
    //单点执行失败率
    public static double fC = 0;
    //本地计算单位处理花销
    public static double cl = 0;
    //自组织网传输单位能耗
    public static double er = 0;

    //一个非常小但是大于0的值 add by adhoc
    public static double epsilon;
    //泊松过程参数 add by adhoc
    public static double lambda;
}
