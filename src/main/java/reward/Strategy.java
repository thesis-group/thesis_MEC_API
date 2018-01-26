package reward;

import model.Task;
import param.*;
import tools.Calculus;

import java.util.function.Function;

public enum Strategy implements StrategyService {
    // 本地卸载
    Local {
        public RewardBackValue calculateReward(Task task, RewardParam param) {
            assert param instanceof LocalParam : "param do not instance of LocalParam";
            LocalParam localParam = (LocalParam)param;

            //计算可执行次数k_
            int k_=(int)(task.getRest()/(task.getWl()/Argument.sCpu));
            k_=k_>task.getK()?task.getK():k_;

            //计算失误率
            double failureRate=Math.pow(localParam.fl,k_);

            //计算失误惩罚
            double Fsch=localParam.cPen*failureRate;

            //计算RTT 和 eCpu
            double RTT=task.getWait();
            double Ecpu=localParam.eCpu;
            double temp=(1-localParam.fl)*(task.getWl()/Argument.sCpu);
            double middleValue=0;
            for(int i=1;i<=k_;i++){
                middleValue+=Math.pow(localParam.fl,i-1)*i*temp;
            }
            RTT+=middleValue;
            Ecpu*=middleValue;

            //计算执行花销
            double Csch=(task.getWl()/Argument.sCpu)*localParam.cl;

            //计算代价
            double cost=Argument.wL1*Ecpu+Argument.wL2*Csch+Argument.wL3*Fsch;

            //返回值
            RewardBackValue rewardBackValue=new RewardBackValue(failureRate,RTT,cost);
            return rewardBackValue;
        }
    },
    // 薄云卸载
    Cloudlet {
        public RewardBackValue calculateReward(Task task, RewardParam param) {
            assert param instanceof CloudletParam : "param do not instance of CloudletParam";
            CloudletParam cloudletParam = (CloudletParam)param;

            //计算上行传输错误概率
            double pUp=cloudletParam.fUp*(1-cloudletParam.fDown)/(1-cloudletParam.fUp*cloudletParam.fDown);
            //计算下行传输错误概率
            double pDown=cloudletParam.eDown*(1-cloudletParam.fUp)/(1-cloudletParam.fUp*cloudletParam.fDown);
            //计算任务一次尝试执行的期望时间
            double Etime=pUp*(task.getIp()/cloudletParam.rUp)*cloudletParam.delta+(1-pUp)*((task.getIp()/cloudletParam.rUp)*cloudletParam.delta+task.getWl()/cloudletParam.sCloudlet+(task.getOp()/cloudletParam.rDown)*cloudletParam.delta);

            //计算可执行次数k_
            int k_=(int)(task.getRest()/Etime);
            k_=k_>task.getK()?task.getK():k_;

            //计算期望能耗
            double Eenergy=cloudletParam.eUp*(task.getIp()/cloudletParam.rUp)*cloudletParam.delta+(1-pUp)*(cloudletParam.eUp*(task.getIp()/cloudletParam.rUp)*cloudletParam.delta+task.getWl()/cloudletParam.sCloudlet+cloudletParam.eDown*(task.getOp()/cloudletParam.rDown)*cloudletParam.delta);

            //计算卸载到薄云的故障率
            double pCloudlet=(cloudletParam.fUp*(1-cloudletParam.fDown)+cloudletParam.fDown*(1-cloudletParam.fUp))/(1-cloudletParam.fUp*cloudletParam.fDown);

            //计算失败率
            double failureRate=Math.pow(pCloudlet,k_);
            //计算失败惩罚
            double Fsch=cloudletParam.cPen*failureRate;
            //计算RTT
            double RTT=task.getWait()+k_*Etime;
            //计算能耗期望
            double Ecom=Eenergy*k_;

            //计算代价
//            double cost=Argument.wCL1*Ecom+Argument.wCL2*Csch+Argument.wCL3*Fsch;

            //返回值
//            RewardBackValue rewardBackValue=new RewardBackValue(failureRate,RTT,cost);
//            return rewardBackValue;
            return null;
        }
    },
    // 自组织网卸载
    AdHoc {
        public RewardBackValue calculateReward(Task task, RewardParam param) {
            assert param instanceof AdHocParam : "param do not instance of AdHocParam";
            AdHocParam adHocParam = (AdHocParam)param;
            // 假设 该参数不为空
            assert adHocParam.sad.size() != 0 : "adHocParam.sad cannot be empty";
            double sadSum = adHocParam.sad.stream().reduce((a, b) -> a + b).get();
            double eSpeed = adHocParam.ps * sadSum / adHocParam.l;
            //计算超时失败率
            double pHt = 2 * adHocParam.p2 * adHocParam.p3;
            double pVt = 2 * adHocParam.p1 * adHocParam.p3 + 2 * adHocParam.p1 * adHocParam.p2;

            //计算瑞利分布函数
            Function<Double,Double> f =
                    x -> (x*x/adHocParam.sigma)*Math.exp(-x*x/(2*adHocParam.sigma*adHocParam.sigma));

            //通过自组织网的一次执行时间 这里原公式E_cp 就当做是E_speed了
            double taoAd = adHocParam.delta * task.getIp() / adHocParam.rup
                    + task.getWl() / eSpeed
                    + adHocParam.delta *  task.getOp()/adHocParam.rdown;
            //水平情况
                //呈同一方向  假设v1和v2都是标量
                double v = Math.abs(adHocParam.v1 - adHocParam.v2);
                double finalV = v;
                Function<Double, Double> T101 = x-> ((adHocParam.R - adHocParam.r) -x)/(finalV + Argument.epsilon);
                double f0s = Calculus.Integrate(
                        x->((T101.apply(x)-taoAd)/T101.apply(x)) * f.apply(x),
                        0 ,
                        adHocParam.r + adHocParam.R
                );
                //呈不同方向
                v = adHocParam.v1 + adHocParam.v2;
                double finalV1 = v;
                Function<Double, Double> T102 = x-> ((adHocParam.R - adHocParam.r) +x)/(finalV1 + Argument.epsilon);
                double f0d = Calculus.Integrate(
                        x->((T102.apply(x)-taoAd)/T102.apply(x)) * f.apply(x),
                        0 ,
                        adHocParam.r + adHocParam.R
                );
            //垂直情况 d不知道什么 觉得可能还是x吧
            v = Math.sqrt(adHocParam.v1*adHocParam.v1 + adHocParam.v2*adHocParam.v2);
            double finalV2 = v;
            Function<Double, Double> T11 = x-> ((adHocParam.R - adHocParam.r) +x)/(finalV2 + Argument.epsilon);
            double f1 = Calculus.Integrate(
                    x->((T11.apply(x)-taoAd)/T11.apply(x)) * f.apply(x),
                    0 ,
                    adHocParam.r + adHocParam.R
            );
            //综合的期望失败率
            double pOf = pHt * (f0s+f0d) + pVt * f1;





            return null;
        }

    };
}
