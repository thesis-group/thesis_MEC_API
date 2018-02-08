package reward.offline;

import model.State;
import model.Task;
import param.AdHocParam;
import param.CloudletParam;
import param.LocalParam;
import param.RewardParam;
import reward.*;

public class RewardOFL {
    public static RewardBackValue getReward(State state, StrategyOFL strategy , Task task){
        RewardParam rewardParam=null;
        double delta=1.0/state.getN();//计算拥塞程度
        switch (strategy){
            case Local:
                rewardParam=new LocalParam();
                break;
            case Cloudlet:
                CloudletParam.delta=delta;
                rewardParam=new CloudletParam();
                break;
            case AdHoc:
                AdHocParam.delta=delta;
                rewardParam=new AdHocParam();
                break;
        }
        return  strategy.calculateReward(task, rewardParam);
    }
}
