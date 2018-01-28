package param;

import java.util.List;

public class AdHocParam extends RewardParam{
    public static double fup;      //数据上传失误率 .
    public static double fdown;    //数据下载的失误率 .
    public static double cCloudlet; //单位处理花销（下标为文档所给下标，没有修改） .
    public static double eup;      //上传单位数据传输能耗 .
    public static double edown;    //下载单位数据传输能耗 .
    public static double cPen;     //单位失败乘法 .
    public static double rup;      //上传数据传输率 .
    public static double rdown;    //下载数据传输率 .
    public static double rad;      //自组织网数据传输率 .
    public static List<Double> sad;     //服务节点处理速度 .
    public static double l;       //提供服务的节点数 .
    public static double p1;       //节点直行概率 .
    public static double p2;       //节点左转概率 .
    public static double p3;       //节点右转概率 .
    public static double cad;      //单位处理花销 .
    public static double v1;       //用户移动速度
    public static double v2;       //服务节点移动速度
    public static double r;        //用户通讯半径
    public static double R;        //服务节点通讯半径
    public static double ps;       //服务率
    public static double sigma;    //瑞利分布参数

    public static double delta;    //拥塞程度
}
