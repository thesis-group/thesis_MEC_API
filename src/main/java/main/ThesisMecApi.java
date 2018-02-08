package main;

import property.PropertyTable;
import property.TrainingTable;
import rl.Learning;
import simulation.*;

public class ThesisMecApi {
    public static void main(String[] args) {
        PropertyTable.init();
        train();
        //TODO 调用调度方法
        SimulationsInput.simulationStart();
    }

    public static void train(){
        switch (TrainingTable.algorithm){
            case 0: //贪心算法
                Learning.greedy.train();
                break;
            case 1: //softmax算法
                Learning.softmax.train();
                break;
        }
    }
}
