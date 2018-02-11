package reward;

import model.Task;
import org.apache.commons.math3.special.Erf;
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
            double RTT=0.0;
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
            double pDown=cloudletParam.fDown*(1-cloudletParam.fUp)/(1-cloudletParam.fUp*cloudletParam.fDown);
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
            double RTT=0.0+k_*Etime;
            //计算能耗期望
            double Ecom=Eenergy*k_;

            //计算支付
            double Cpay=cloudletParam.cCloudlet*task.getWl()/cloudletParam.sCloudlet;

            //计算代价
            double cost=Argument.wCL1*Ecom+Argument.wCL2*Cpay+Argument.wCL3*Fsch;

            //返回值
            RewardBackValue rewardBackValue=new RewardBackValue(failureRate,RTT,cost);
            return rewardBackValue;
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
            double pHt = Math.pow(adHocParam.p1,2) + Math.pow(adHocParam.p1,2) +  Math.pow(adHocParam.p1,2)
                        +2 * adHocParam.p2 * adHocParam.p3;
            double pVt = 2 * adHocParam.p1 * adHocParam.p3 + 2 * adHocParam.p1 * adHocParam.p2;

            //计算瑞利分布函数
//            Function<Double,Double> f =
//                    x -> (x*x/adHocParam.sigma)*Math.exp(-x*x/(2*adHocParam.sigma*adHocParam.sigma));

            //通过自组织网的一次执行时间 这里原公式E_cp 就当做是E_speed了
            double taoAd = task.getIp() / adHocParam.rad
                    + task.getWl() / eSpeed
                    + task.getOp()/adHocParam.rad;

            //通过接入网的执行的一次执行时间
            double taoRan = 2 * adHocParam.delta * task.getIp() / adHocParam.rup
                    + task.getWl() / eSpeed
                    + 2 * adHocParam.delta *  task.getOp() / adHocParam.rdown;

            double middle1 = 0.0;

            middle1 = Erf.erf(1/(Math.sqrt(2)*Math.abs(AdHocParam.sigma))*(adHocParam.R + adHocParam.r))
                    - Erf.erf(0);
            //水平情况
                //呈同一方向  假设v1和v2都是标量
                double v = Math.abs(adHocParam.v1 - adHocParam.v2);
                double m1 = adHocParam.R + adHocParam.r;
                double f0s = Math.sqrt(Math.PI)*Math.pow(AdHocParam.sigma,4) * middle1
                        - Math.pow(adHocParam.R + adHocParam.r, 2)
                        * Math.exp(-(Math.pow(adHocParam.R + adHocParam.r, 2))/2 * adHocParam.sigma*adHocParam.sigma)
                        + (v + Argument.epsilon) * taoAd
                        * (Math.exp(-4) * m1 * Math.pow(adHocParam.R + adHocParam.r, 2)
                        +Math.exp(-6)*Math.pow(adHocParam.sigma,4)*Math.pow(m1,-2)*Math.pow(adHocParam.R + adHocParam.r, 3)
                        +0.5*Math.exp(-8)*Math.pow(m1,-1)*Math.pow(adHocParam.R + adHocParam.r, 4));
                //呈不同方向
                v = adHocParam.v1 + adHocParam.v2;
                double m2 = -(adHocParam.R + adHocParam.r);
//
                double f0d =  Math.sqrt(Math.PI)*Math.pow(AdHocParam.sigma,4) * middle1
                        - Math.pow(adHocParam.R + adHocParam.r, 2)
                        * Math.exp(-(Math.pow(adHocParam.R + adHocParam.r, 2))/2 * adHocParam.sigma*adHocParam.sigma)
                        + (v + Argument.epsilon) * taoAd
                        * (Math.exp(-4) * m2 * Math.pow(adHocParam.R + adHocParam.r, 2)
                        +Math.exp(-6)*Math.pow(adHocParam.sigma,4)*Math.pow(m2,-2)*Math.pow(adHocParam.R + adHocParam.r, 3)
                        +0.5*Math.exp(-8)*Math.pow(m2,-1)*Math.pow(adHocParam.R + adHocParam.r, 4));;
            //垂直情况 d不知道什么 觉得可能还是x吧 TODO d是个啥
            v = Math.sqrt(adHocParam.v1*adHocParam.v1 + adHocParam.v2*adHocParam.v2);
            double f1 = Math.sqrt(Math.PI)*Math.pow(AdHocParam.sigma,4) * middle1
                    - Math.pow(adHocParam.R + adHocParam.r, 2)
                    * Math.exp(-(Math.pow(adHocParam.R + adHocParam.r, 2))/2 * adHocParam.sigma*adHocParam.sigma)
                    + (v + Argument.epsilon) * taoAd
                    * (Math.exp(-4) * m1 * Math.pow(adHocParam.R + adHocParam.r, 2)
                    +Math.exp(-6)*Math.pow(adHocParam.sigma,4)*Math.pow(m1,-2)*Math.pow(adHocParam.R + adHocParam.r, 3)
                    +0.5*Math.exp(-8)*Math.pow(m1,-1)*Math.pow(adHocParam.R + adHocParam.r, 4));
            //综合的期望失败率
            double pOf = pHt * (f0s+f0d) + pVt * f1;
            //TODO 我理解用来计算n要用的是泊松过程的期望
            Function<Double,Double> poison = x-> Argument.lambda;
            double n = poison.apply(taoAd);

            //计算执行失败率
            double pCom= 1 - Math.pow((1-Argument.fC),n*adHocParam.ps);
            //卸载到其他节点的失败率
            double pAd= (pCom * (1 - pOf)+(1-pCom) *pOf)/(1-pCom*pOf);
            //通过接入网卸载的几率为
            double pRan = (adHocParam.fup * (1 - adHocParam.fdown) * (1 - pCom)
                    + (1 - adHocParam.fup) * adHocParam.fdown )
                    / (1 - adHocParam.fdown * adHocParam.fup * (1 - pCom)
                    - adHocParam.fup * pCom * (1 - adHocParam.fdown)
                    - adHocParam.fup * adHocParam.fdown * pCom);


            //TODO 计入网执行时间的期望 貌似没有用到

            //是否选择自组织网 //TODO 分母一样就不要了？
            boolean isAd = taoAd * pAd < taoRan * pRan;

            // 如果选择自组织网
            if( isAd ){
                // 一次执行时间
                double time = taoAd;
                int kp = (int)(task.getRest()/time);
                double fsch = adHocParam.cPen * Math.pow(1-pAd, kp);
                // TODO 这里需要wait？ 不需要!
                double rtt = 0.0;
                //计算期望的能耗 TODO er没放在输出 放在argument里了
                double eComp = (Argument.er * task.getIp()/adHocParam.rad)
                        + (Argument.er * task.getOp()/adHocParam.rad);
                for(int i = 1 ; i <= kp ; i++){
                    // 已经按照最新的公式进行修改
                    rtt += Math.pow((pAd),i-1) * i * time * (1 - pAd);
                    eComp *= Math.pow((1 - pAd),i) * i;

                }
                double cPay = adHocParam.cad * adHocParam.delta * (Argument.er * task.getIp()/adHocParam.rad)
                        + (Argument.er * task.getOp()/adHocParam.rad);
                double cost = Argument.wAD1 * eComp + Argument.wAD2 * cPay + Argument.wAD3 * fsch;
                return new RewardBackValue(fsch , rtt , cost);
            }
            // 如果选择接入网
            else {
                // 一次执行时间
                double time = taoRan;
                int kp = (int)(task.getRest()/time);
                double fsch = adHocParam.cPen * Math.pow(1-pRan, kp);
                // TODO 这里需要wait？
                double rtt = 0.0;
                //计算期望的能耗
                double eComp = (adHocParam.eup * task.getIp()/adHocParam.rup)
                        + (adHocParam.edown * task.getOp()/adHocParam.rdown);
                // 已经按照最新的公式进行修改
                for(int i = 1 ; i <= kp ; i++){
                    rtt += Math.pow((pAd),i-1) * i * time * (1 - pAd);
                    eComp *= Math.pow((1 - pAd),i) * i;

                }
                double cPay = adHocParam.cad * adHocParam.delta * ((adHocParam.eup * task.getIp()/adHocParam.rup)
                        + (adHocParam.edown * task.getOp()/adHocParam.rdown));

                double cost = Argument.wAD1 * eComp + Argument.wAD2 * cPay + Argument.wAD3 * fsch;
                return new RewardBackValue(fsch, rtt, cost);
            }

        }


        private int stage(int n){
            int ans = 1;
            for(int i = 2 ; i<= n; i++){
                ans *= i;
            }
            return ans;
        }

    };
}
