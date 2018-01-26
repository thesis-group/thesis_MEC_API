package reward;

import model.Task;
import param.AdHocParam;
import param.Argument;
import param.LocalParam;
import param.RewardParam;
import tools.Calculus;

import java.util.function.Function;

public enum Strategy implements StrategyService {
    // 本地卸载
    Local {
        public RewardBackValue calculateReward(Task task, RewardParam param) {
            assert param instanceof LocalParam : "param do not instance of LocalParam";
            LocalParam localParam = (LocalParam)param;
            int k_=(int)(task.getRest()/(task.getWl()/Argument.sCpu));
            return null;
        }
    },
    // 薄云卸载
    Cloudlet {
        public RewardBackValue calculateReward(Task task, RewardParam param) {
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

            //TODO E_cp
            //通过自组织网的一次执行时间 这里原公式E_cp 就当做是E_speed了
            double taoAd = task.getIp() / adHocParam.rad
                    + task.getWl() / eSpeed
                    + task.getOp()/adHocParam.rad;

            //通过接入网的执行的一次执行时间
            double taoRan = adHocParam.delta * task.getIp() / adHocParam.rup
                    + task.getWl() / eSpeed
                    + adHocParam.delta *  task.getOp()/adHocParam.rdown;

            //TODO 曼哈顿模型
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
            //垂直情况 d不知道什么 觉得可能还是x吧 TODO d是个啥
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
                // 一次执行时间  TODO r_ch是个傻我觉得是rad 所以直接用上面算的了
                double time = taoAd;
                int kp = (int)(task.getRest()/time);
                double fsch = adHocParam.cPen * Math.pow(1-pAd, kp);
                // TODO 这里需要wait？
                double rtt = task.getWait();
                //计算期望的能耗 TODO er没放在输出 放在argument里了
                double eComp = (Argument.er * task.getIp()/adHocParam.rad)
                        + (Argument.er * task.getOp()/adHocParam.rad);
                for(int i = 1 ; i <= kp ; i++){
                    rtt += Math.pow((1 - pAd),i) * i * time;
                    eComp *= Math.pow((1 - pAd),i) * i;

                }
                double cPay = adHocParam.cad * adHocParam.delta * (Argument.er * task.getIp()/adHocParam.rad)
                        + (Argument.er * task.getOp()/adHocParam.rad);
            }
            // 如果选择接入网
            else {
                // 一次执行时间
                double time = taoRan;
                int kp = (int)(task.getRest()/time);
                double fsch = adHocParam.cPen * Math.pow(1-pRan, kp);
                // TODO 这里需要wait？
                double rtt = task.getWait();
                //计算期望的能耗
                double eComp = (adHocParam.eup * task.getIp()/adHocParam.rup)
                        + (adHocParam.edown * task.getOp()/adHocParam.rdown);
                // TODO 原公式有问题？ kp 还是 kp-1
                for(int i = 1 ; i <= kp ; i++){
                    rtt += Math.pow((1 - pAd),i) * i * time;
                    eComp *= Math.pow((1 - pAd),i) * i;

                }
                double cPay = adHocParam.cad * adHocParam.delta * ((adHocParam.eup * task.getIp()/adHocParam.rup)
                        + (adHocParam.edown * task.getOp()/adHocParam.rdown));
            }






            return null;
        }

    };
}
