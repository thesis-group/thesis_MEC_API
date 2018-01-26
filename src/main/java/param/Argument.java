package param;

public class Argument {
    public static double sCpu; //本地执行所需要的cpu add by local
    public static double wL1;  //本地执行用户设备处理器能耗的权重 add by local
    public static double wL2;  //本地执行调度的权重 add by local
    public static double wL3;  //本地执行成功概率的权重 add by local
    public static double wCL1;  //卸载到薄云能耗的权重 add by cloudlet
    public static double wCL2;  //卸载到薄云惩罚的权重 add by cloudlet
    public static double wCL3;  //卸载到薄云成功执行概率的权重 add by cloudlet
    public static double epsilon; //一个非常小但是大于0的值 add by adhoc
    public static double fC; //单点执行失败率 add by adhoc
}
