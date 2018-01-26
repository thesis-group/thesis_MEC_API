package model;

import reward.Strategy;
import rl.Learning;

import java.util.Map;

public class Result {
    //训练结果模型 （状态id -> (策略 ->  对应的reward/cost))
    // public  Map<Long, Map<Strategy,Double>> resultModel;
    //模型保存的文件名

    private static Learning method; //选择使用哪种模型进行训练

    public static void  setType(Learning way){
            method = way;
    }

    /**
     * 将训练的一个state 保存答配置文件指定的路径
     * @param stateID
     * @param action
     */

    public static  void save(Long stateID , Map<Strategy,Double>  action){

    }

    /**
     * 从配置文件指定的路径的路径中获取模型（用于调度）
     */
    public  Map<Long, Map<Strategy,Double>>  load(){
        return null;
    }


}
