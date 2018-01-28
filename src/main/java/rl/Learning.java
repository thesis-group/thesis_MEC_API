package rl;

import model.Result;
import model.State;
import model.Task;
import param.Argument;
import param.TrainParam;
import property.TrainingTable;
import reward.Strategy;
import tools.ActionFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public enum Learning  {
    greedy{

        Task trainingTask;
        public void train(){
            long result = Result.getTrainedProcess();
            for(long i = result ; i < 20*50*1000 ; i++){
                trainState(i);
            }
        }

        private void trainState(Long stateID){
            State state = new State(stateID);
            Map<Strategy, Integer> count = new HashMap<>();
            Map<Strategy, Integer> reward = new HashMap<>();
            List<Strategy> possibleAction =ActionFilter.getPossibleAction(state);
            possibleAction.forEach(t->count.put(t,0));
            possibleAction.forEach(t->reward.put(t,0));
            Random random = new Random();
            Strategy choosed ;

            // 选择那个一个action
            if(random.nextDouble() < TrainParam.epsilon){
                choosed = possibleAction.get(random.nextInt(possibleAction.size()));
            }else{
               double  cost = 200000.0;
               for(Strategy s : possibleAction){
                    if(reward.get(s) < cost){
                        cost = reward.get(s);
                        choosed = s;
                    }
               }
            }
            int wait = state.
//            Task curTask = new Task(TrainParam.lifespan - .)






        }
    },
    softmax{
        public void train(){
            long result = Result.getTrainedProcess();
            for(long i = result ; i < 20*50*1000 ; i++){
                trainState(i);
            }
        }

        private void trainState(Long stateID){

        }
    }
}
