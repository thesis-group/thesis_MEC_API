package property;

import param.AdHocParam;
import param.Argument;
import param.CloudletParam;
import param.LocalParam;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 参数类，使用init方法进行配置参数的初始化
 */
public class PropertyTable {
    static Object[] tables = new Object[]{new VariableTable(), new FixParameterTable(), new GivenParameterTable(), new TrainingTable()};

    /**
     * 初始化配置文件和param
     */
    public static void init() {
        initProperty();
        initParameters();
    }

    public static void main(String[] args){
        initProperty();
        System.out.println(VariableTable.sAdK.get(0));
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
        Argument.wL1=GivenParameterTable.wL1;
        Argument.wL2=GivenParameterTable.wL2;
        Argument.wL3=GivenParameterTable.wL3;
        Argument.wCL1=GivenParameterTable.wCL1;
        Argument.wCL2=GivenParameterTable.wCL2;
        Argument.wCL3=GivenParameterTable.wCL3;
        Argument.wAD1=GivenParameterTable.wAD1;
        Argument.wAD2=GivenParameterTable.wAD2;
        Argument.wAD3=GivenParameterTable.wAD3;

        LocalParam.fl=VariableTable.fl;
        CloudletParam.fUp=VariableTable.fUp;
        AdHocParam.fup=VariableTable.fUp;
        CloudletParam.fDown=VariableTable.fDown;
        AdHocParam.fdown=VariableTable.fDown;
        AdHocParam.cPen=VariableTable.cPen;
        CloudletParam.cPen=VariableTable.cPen;
        LocalParam.cPen=VariableTable.cPen;
        AdHocParam.rad=VariableTable.rAd;
        AdHocParam.cad=VariableTable.cAd;
        AdHocParam.cCloudlet=VariableTable.cCloudlet;
        CloudletParam.cCloudlet=VariableTable.cCloudlet;
        CloudletParam.sCloudlet=VariableTable.sCloudlet;
        AdHocParam.eup=VariableTable.eUp;
        AdHocParam.edown=VariableTable.eDown;
        CloudletParam.eUp=VariableTable.eUp;
        CloudletParam.eDown=VariableTable.eDown;
        AdHocParam.rup=VariableTable.rUp;
        AdHocParam.rdown=VariableTable.rDown;
        CloudletParam.rUp=VariableTable.rUp;
        CloudletParam.rDown=VariableTable.rDown;
        LocalParam.eCpu=VariableTable.eCpu;
        Argument.sCpu=VariableTable.sCpu;

    }

}
