package property;

import java.lang.reflect.Field;
import java.util.ResourceBundle;

/**
 * 参数类，使用init方法进行配置参数的初始化
 */
public class PropertyTable {
    static Object[] tables = new Object[]{new VariableTable(), new FixParameterTable(), new GivenParameterTable(), new TrainingTable()};

    /**
     * 初始化配置文件
     */
    public static void init() {
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
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
