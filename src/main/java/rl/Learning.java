package rl;

import model.Result;
import model.State;
import model.Task;
import param.TrainParam;
import reward.Reward;
import reward.Strategy;
import tools.ActionFilter;

import java.util.*;

public enum Learning implements LearningService  {
    greedy{
        public void train(){
            long result = Result.getTrainedProcess()+1;
            for(long i = result ; i < 20*50*1000 ; i++){
                trainState(i);
            }
        }

        private void trainState(Long stateID){
            State state = new State(stateID);
            Map<Strategy, Integer> count = new HashMap<>();
            Map<Strategy, Double> reward = new HashMap<>();
            List<Strategy> possibleAction =ActionFilter.getPossibleAction(state);
            possibleAction.forEach(t->count.put(t,0));
            possibleAction.forEach(t->reward.put(t,0.0));
            Random random = new Random();

            for(int i = 0 ; i < TrainParam.iter ; i++){
                Strategy choosed = null;
                // 选择那个一个action
                if(random.nextDouble() < TrainParam.epsilon){
                    choosed = possibleAction.get(random.nextInt(possibleAction.size()));
                }else{ 
                    double cost = 200000.0;
                    for(Strategy s : possibleAction){
                        if(reward.get(s) < cost){
                            cost = reward.get(s);
                            choosed = s;
                        }
                    }
                }
                double  wait = state.getQ()*TrainParam.rtt;
                double  rest = TrainParam.lifespan - wait;
                count.put(choosed,count.get(choosed)+1);
                Task curTask = new Task((int)rest, TrainParam.k,TrainParam.wl,TrainParam.ip,TrainParam.op, wait);
                double cost =  Reward.getReward(state,choosed,curTask).getCost();
                double newcost = (reward.get(choosed)*(count.get(choosed)-1) + cost)/count.get(choosed);
                reward.put(choosed,newcost);
            }

            Result.save(stateID,reward);
        }
    },
    softmax{
        public void train(){
            long result = Result.getTrainedProcess()+1;
            for(long i = result ; i < 20*50*1000 ; i++){
                trainState(i);
            }
        }

        private void trainState(Long stateID){
            State state = new State(stateID);
            Map<Strategy, Integer> count = new HashMap<>();
            Map<Strategy, Double> reward = new HashMap<>();
            Map<Strategy, Double> prob = new HashMap<>();

            List<Strategy> possibleAction =ActionFilter.getPossibleAction(state);
            possibleAction.forEach(t->count.put(t,0));
            possibleAction.forEach(t->reward.put(t,0.0));
            possibleAction.forEach(t->prob.put(t,1.0/possibleAction.size()));
            Random random = new Random();

            for(int i = 0 ; i < TrainParam.iter ; i++){
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

                double  wait = state.getQ()*TrainParam.rtt;
                double  rest = TrainParam.lifespan - wait;
                count.put(choosed,count.get(choosed)+1);
                Task curTask = new Task((int)rest, TrainParam.k,TrainParam.wl,TrainParam.ip,TrainParam.op, wait);
                double cost =  Reward.getReward(state,choosed,curTask).getCost();
                double newcost = (reward.get(choosed)*(count.get(choosed)-1) + cost)/count.get(choosed);
                reward.put(choosed,newcost);
            }

            Result.save(stateID,reward);
        }


    }
}
