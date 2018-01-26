package property;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

public class VariableTable {

    public static void init(){

    }
    public static void main(String[] args){
        ResourceBundle resourceBundle=ResourceBundle.getBundle("VariableTable");
        Enumeration<String> allkey=resourceBundle.getKeys();
        List<String> list=new ArrayList<>();
        while (allkey.hasMoreElements()){
            String key=allkey.nextElement();
            String value=resourceBundle.getString(key);
            System.out.println(key+"="+value);
        }
    }
}
