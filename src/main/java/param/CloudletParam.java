package param;

public class CloudletParam extends RewardParam {
    public static double fUp;      //数据上传的故障率 .
    public static double fDown;    //数据下载的故障率 .
    public static double cCloudlet;//单位处理花销 .
    public static double eUp;      //单位数据传输能耗（上传） .
    public static double eDown;    //单位数据传输能耗（下载） .
    public static double cPen;     //单位失败惩罚 .
    public static double sCloudlet;//薄云处理速度 .
    public static double rUp;      //数据传输率（上传） .
    public static double rDown;    //数据传输率（下载） .

    public static double delta;    //拥塞程度
}
