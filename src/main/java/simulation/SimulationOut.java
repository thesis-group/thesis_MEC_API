package simulation;

import java.io.*;

public class SimulationOut {
	
	
	public static void output(int[] per, double totalfsch, int[] taskPosition, int Tmax) {
		try {
		
		File writename = new File("D:\\output.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件  
        writename.createNewFile(); // 创建新文件  
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));  
        for(int i = 1; i <= Tmax; i++) {
        	out.write(i + "  " + taskPosition[i] +"\r\n"); 
        }
        out.write("本地 = "+ per[0]/(double)Tmax + "云端 = "+ per[1]/(double)Tmax + "自组织 = "+ per[2]/(double)Tmax + "\r\n");
        out.write("totalfsch = "+ totalfsch + "\r\n"); // \r\n即为换行  
        
        out.flush(); // 把缓存区内容压入文件  
        out.close(); // 最后记得关闭文件  
        
        
        
	 } catch (Exception e) {  
         e.printStackTrace();  
     }  
	}
}
