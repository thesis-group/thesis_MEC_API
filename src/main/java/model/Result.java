package model;

import org.json.JSONObject;
import reward.Strategy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result {
    //训练结果模型 （状态id -> (策略 ->  对应的reward/cost))
    // public  Map<Long, Map<Strategy,Double>> resultModel;
    //模型保存的文件名

    static File  file;
    static BufferedWriter bf;

    /**
     * 指定 保存结果的文件名
     * @param filename
     */
    public static void  setFile(String filename){
        file = new File(filename);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 设置输出流
        try {
            bf = new BufferedWriter( new FileWriter(filename,true));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 将训练的一个state 保存答配置文件指定的路径
     * @param stateID
     * @param action
     */

    public static  void save(Long stateID , Map<Strategy,Double>  action){
        JSONObject json = new JSONObject(action);
        String line = stateID+"->"+json.toString();
        try {
            bf.append(line);
            bf.newLine();
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 调用训练好的模型（进行模拟）
     * @return  Map<Long, Map<Strategy,Double>>
     */
    public static  Map<Long, Map<Strategy,Double>>  load(){
        Map<Long, Map<Strategy,Double>> map = new HashMap<>();
        //TODO换成配置文件中需要的内容
        List<String> lines = null;
        try {
             lines = Files.readAllLines(Paths.get("test.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert lines != null && lines.size() > 0 :"训练集不能为空";
        for(String line: lines ){
            String[] sp  = line.split("->");
            JSONObject jsonObject = new JSONObject(sp[1]);
            Map<Strategy, Double> action = new HashMap<>();
            for(Strategy s : Strategy.values()){
                if(jsonObject.has(s.toString())){
                    action.put(s,jsonObject.getDouble(s.toString()));
                }
            }
            map.put(Long.valueOf(sp[0]),action);
        }

        return map;
    }

    /**
     * 获取之前训练的进度
     * 用于恢复训练
     * @return 之前训练的ID进度
     */
    public static long  getTrainedProcess(){
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get("test.txt"), StandardCharsets.UTF_8);
            return lines.size()-1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }



}
