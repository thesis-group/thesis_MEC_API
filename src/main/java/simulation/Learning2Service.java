package simulation;

import java.util.Map;

import reward.Strategy;

public interface Learning2Service {

    public Strategy trainState(Map<Long, Map<Strategy, Double>> map, Long stateID);
}
