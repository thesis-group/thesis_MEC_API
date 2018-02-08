package simulation;

import reward.Strategy;

public interface Learning2Service {

    public Strategy trainState(Long stateID);
}
