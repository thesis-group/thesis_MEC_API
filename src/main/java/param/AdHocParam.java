package param;

import java.util.List;

public class AdHocParam extends RewardParam{
    public double fup;      //数据上传失误率
    public double fdown;    //数据下载的失误率
    public double cCloudlet; //单位处理花销（下标为文档所给下标，没有修改）
    public double eup;      //上传单位数据传输能耗
    public double edown;    //下载单位数据传输能耗
    public double cPen;     //单位失败乘法
    public double delta;    //拥塞程度
    public double rup;      //上传数据传输率
    public double rdown;    //下载数据传输率
    public double rad;      //自组织网数据传输率
    public List<Double> sad;     //服务节点处理速度
    public double ps;       //服务率
    public  double l;       //提供服务的节点数
    public double p1;       //节点直行概率
    public double p2;       //节点左转概率
    public double p3;       //节点右转概率
    public double sigma;    //瑞利分布参数
    public double v1;       //用户移动速度
    public double v2;       //服务节点移动速度
    public double cad;      //单位处理花销
    public double r;        //用户通讯半径
    public double R;        //服务节点通讯半径


}
