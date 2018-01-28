package property;

import model.GroundMap;
import model.Result;
import param.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 参数类，使用init方法进行配置参数的初始化
 */
public class PropertyTable {
    static Object[] tables = new Object[]{new VariableTable(), new FixParameterTable(), new GivenParameterTable(), new TrainingTable(),new GroundMap()};

    /**
     * 初始化配置文件和param
     */
    public static void init() {
        initProperty();
        initParameters();
    }

    /**
     * 初始化property包内的其他类
     */
    private static void initProperty(){
        for (Object table : tables) {
            String tableName = table.toString().split("\\.")[1].split("@")[0];
            ResourceBundle resourceBundle = ResourceBundle.getBundle(tableName);
            Field[] fields = table.getClass().getFields();
            for (Field field : fields) {
                String name = field.getName();
                String type = field.getGenericType().getTypeName();
                try {
                    switch (type) {
                        case "double":
                            field.set(table, Double.valueOf(resourceBundle.getString(name)));
                            break;
                        case "int":
                            field.set(table, Integer.valueOf(resourceBundle.getString(name)));
                            break;
                        case "java.lang.String":
                            field.set(table, resourceBundle.getString(name));
                            break;
                        case "boolean":
                            field.set(table, Boolean.valueOf(resourceBundle.getString(name)));
                            break;
                        case "java.util.List<java.lang.Double>":
                            List<Double> list=new ArrayList<>();
                            String[] values=resourceBundle.getString(name).split(",");
                            for(String s:values){
                                list.add(Double.valueOf(s));
                            }
                            field.set(table,list);
                            break;
                        case "java.util.Map<java.lang.Integer, java.lang.Boolean>":
                            Map<Integer,Boolean> map=new HashMap<>();
                            String[] mapValues=resourceBundle.getString(name).split(",");
                            for(int i=0;i<50;i++) map.put(i,false);
                            for (String s:mapValues) map.put(Integer.valueOf(s),true);
                            field.set(table,map);
                            break;
                        default:
                            System.out.println(type);
                            break;
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化param里的类可以确定的变量
     */
    private static void initParameters() {
        AdHocParam.p1=FixParameterTable.p1;
        AdHocParam.p2=FixParameterTable.p2;
        AdHocParam.p3=FixParameterTable.p3;
        AdHocParam.l=FixParameterTable.delte;
        AdHocParam.fup=VariableTable.fUp;
        AdHocParam.fdown=VariableTable.fDown;
        AdHocParam.cPen=VariableTable.cPen;
        AdHocParam.rad=VariableTable.rAd;
        AdHocParam.cad=VariableTable.cAd;
        AdHocParam.cCloudlet=VariableTable.cCloudlet;
        AdHocParam.eup=VariableTable.eUp;
        AdHocParam.edown=VariableTable.eDown;
        AdHocParam.rup=VariableTable.rUp;
        AdHocParam.rdown=VariableTable.rDown;
        AdHocParam.sad=VariableTable.sAdK;
        AdHocParam.r=VariableTable.r;
        AdHocParam.R=VariableTable.R;
        AdHocParam.v1=VariableTable.v1;
        AdHocParam.v2=VariableTable.v2;
        AdHocParam.sigma=FixParameterTable.sigma;
        AdHocParam.ps=FixParameterTable.ps;

        Argument.wL1=GivenParameterTable.wL1;
        Argument.wL2=GivenParameterTable.wL2;
        Argument.wL3=GivenParameterTable.wL3;
        Argument.wCL1=GivenParameterTable.wCL1;
        Argument.wCL2=GivenParameterTable.wCL2;
        Argument.wCL3=GivenParameterTable.wCL3;
        Argument.wAD1=GivenParameterTable.wAD1;
        Argument.wAD2=GivenParameterTable.wAD2;
        Argument.wAD3=GivenParameterTable.wAD3;
        Argument.sCpu=VariableTable.sCpu;
        Argument.er=VariableTable.er;
        Argument.fC=VariableTable.fC;
        Argument.lambda=VariableTable.lambda;
        Argument.epsilon=0.000001;

        LocalParam.fl=VariableTable.fl;
        LocalParam.cPen=VariableTable.cPen;
        LocalParam.eCpu=VariableTable.eCpu;
        LocalParam.cl=VariableTable.cl;

        CloudletParam.fUp=VariableTable.fUp;
        CloudletParam.fDown=VariableTable.fDown;
        CloudletParam.cPen=VariableTable.cPen;
        CloudletParam.cCloudlet=VariableTable.cCloudlet;
        CloudletParam.sCloudlet=VariableTable.sCloudlet;
        CloudletParam.eUp=VariableTable.eUp;
        CloudletParam.eDown=VariableTable.eDown;
        CloudletParam.rUp=VariableTable.rUp;
        CloudletParam.rDown=VariableTable.rDown;

        TrainParam.epsilon=FixParameterTable.epsilon;
        TrainParam.iter=TrainingTable.times;
        TrainParam.ip=TrainingTable.ip;
        TrainParam.op=TrainingTable.op;
        TrainParam.k=TrainingTable.k;
        TrainParam.rest=TrainingTable.rest;
        TrainParam.lifespan=TrainingTable.lifespan;
        TrainParam.rtt=TrainingTable.RTT;
        TrainParam.wl=TrainingTable.wl;
        TrainParam.savepath=TrainingTable.resultFilePath+TrainingTable.resultFileName;
        TrainParam.temp=FixParameterTable.rho;

        Result.setFile(TrainParam.savepath);
    }

}
