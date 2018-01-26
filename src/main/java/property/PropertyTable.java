package property;

/**
 * 参数类，使用init方法把三个
 */
public class PropertyTable {
    public static void init(){
        VariableTable.init();
        FixParameterTable.init();
        GivenParameterTable.init();
    }
}
