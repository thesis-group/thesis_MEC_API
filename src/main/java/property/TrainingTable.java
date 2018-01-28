package property;

/**
 * 用于训练的数据
 */
public class TrainingTable {
    public static int rest; //剩余生命周期
    public static int k;  //最大执行次数上限
    public static double wl; //工作负载
    public static double ip; //输入数据量
    public static double op; //输出数据量
    public static double lifespan;//生命周期
    public static double RTT; //最长执行时间

    public static int times;//迭代次数
    public static int algorithm;//算法种类

    //训练结果信息
    public static String resultFileName;//训练结果文件名
    public static String resultFilePath;//训练结果文件夹路径
}
