package reward;

import model.Task;
import param.RewardParam;

public interface StrategyService {
    /**
     * 计算不同策略获取的reward
     * @param task   当前需要执行的任务task
     * @param param  根据不同策略需要的输入参数
     * @return  RewardBackValue
     */
    RewardBackValue calculateReward(Task task , RewardParam param);
}
