package simulation;

import model.Result;
import model.State;
import model.Task;
import param.TrainParam;
import reward.Reward;
import reward.Strategy;
import tools.ActionFilter;

import java.util.*;

public enum Learning2 implements Learning2Service  {
    greedy{
        public Strategy trainState(Map<Long, Map<Strategy, Double>> map, Long stateID){
            State state = new State(stateID);
            Map<Strategy, Double> reward = new HashMap<>();
            List<Strategy> possibleAction =ActionFilter.getPossibleAction(state);
            possibleAction.forEach(t->reward.put(t,(map.get(stateID)).get(t)));
            Random random = new Random();

           
                Strategy choosed = null;
                // 选择那个一个action
                if(random.nextDouble() < TrainParam.epsilon){
                    choosed = possibleAction.get(random.nextInt(possibleAction.size()));
                }else{ 
                    double cost = Double.POSITIVE_INFINITY;
                    for(Strategy s : possibleAction){
                        if(reward.get(s) < cost){
                            cost = reward.get(s);
                            choosed = s;
                        }
                    }
                }
                             
			return choosed;
        }
    },
    softmax{

        public Strategy trainState(Map<Long, Map<Strategy, Double>> map, Long stateID){
            State state = new State(stateID);
            Map<Strategy, Integer> countnum = new HashMap<>();
            Map<Strategy, Double> reward = new HashMap<>();
            Map<Strategy, Double> prob = new HashMap<>();

            List<Strategy> possibleAction =ActionFilter.getPossibleAction(state);
            possibleAction.forEach(t->reward.put(t,map.get(stateID).get(t)));
            possibleAction.forEach(t->prob.put(t,1.0/possibleAction.size()));
            Random random = new Random();

            
                Strategy choosed = null;
                // 计算BlZM分布
                possibleAction.forEach(t->prob.put(t,Math.exp(reward.get(t)/TrainParam.temp)));
                double sum = possibleAction.stream().mapToDouble(t->reward.get(t)).sum();
                possibleAction.forEach(t->prob.put(t,prob.get(t)/sum));

                
                // 根据分布选择action
                List<Double> list = new ArrayList<>();
                possibleAction.forEach(t -> list.add(prob.get(t)));
                choosed = possibleAction.get(possibleAction.size()-1);
                double rand = random.nextDouble();
                for(int j = 0 ; j < list.size() ; j++){
                    if(rand < list.get(j)){
                        choosed = possibleAction.get(j);
                        break;
                    }
                }

           
                
			return choosed;
        }


    }
}
