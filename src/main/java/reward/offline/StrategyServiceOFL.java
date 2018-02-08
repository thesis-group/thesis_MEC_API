package reward.offline;

import model.Task;
import param.RewardParam;
import reward.*;

public interface StrategyServiceOFL {
    /**
     * 计算不同策略获取的reward
     * @param task   当前需要执行的任务task
     * @param param  根据不同策略需要的输入参数
     * @param times 
     * @return  RewardBackValue
     */
    RewardBackValue calculateReward(Task task , RewardParam param);
}
