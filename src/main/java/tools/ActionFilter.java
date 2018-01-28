package tools;

import model.GroundMap;
import model.State;
import reward.Strategy;

import java.util.ArrayList;
import java.util.List;

public class ActionFilter {
    /**
     * 根据状态获取可能的 action
     * @param state
     * @return possible action list
     */
    public static List<Strategy> getPossibleAction(State state){
        List<Strategy>  ans = new ArrayList<>();
        ans.add(Strategy.Local);
        if(GroundMap.hasCloudlet(state.getZ()))
            ans.add(Strategy.Cloudlet);
        if(state.getV()!=0)
            ans.add(Strategy.AdHoc);
        return ans;

    }
}
