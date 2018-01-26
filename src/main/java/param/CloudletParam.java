package param;

public class CloudletParam extends RewardParam {
    public double fUp;      //数据上传的故障率
    public double fDown;    //数据下载的故障率
    public double cCloudlet;//单位处理花销
    public double eUp;      //单位数据传输能耗（上传）
    public double eDown;    //单位数据传输能耗（下载）
    public double cPen;     //单位失败惩罚
    public double delta;    //拥塞程度
    public double sCloudlet;//薄云处理速度
    public double rUp;      //数据传输率（上传）
    public double rDown;    //数据传输率（下载）
}
