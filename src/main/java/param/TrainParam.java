package param;

public class TrainParam {
    public static String savepath = "./test.txt"; //保存文件路径 原来的路径 + 文件名
    public static int iter ;  // 训练迭代次数
    public static double epsilon; //贪心算法选定的epsilon值

    public static int rest; //剩余生命周期
    public static int k;  //最大执行次数上限
    public static double wl; //工作负载
    public static double ip; //输入数据量
    public static double op; //输出数据量
    public static double rtt; //训练任务指定rtt
    public static double lifespan; //训练指定任务的生命周期
}
