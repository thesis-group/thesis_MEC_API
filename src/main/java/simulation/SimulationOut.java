package simulation;

import java.io.*;

public class SimulationOut {
	
	
	public static void output(int[] per, double totalfsch, int[] taskPosition, double[] tcost, double[] tfail, int Tmax, int j) {
		try {
			File writename = new File("./Result.txt");
		switch(j) {
		case 0:
			writename = new File("./Result.txt"); 
			break;
		case 1:
			writename = new File("./Result-Greedy.txt"); 
			break;
		case 2:
			writename = new File("./Result-Random.txt"); 
		}
        
		
		
		writename.createNewFile(); // 创建新文件  
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));  
        for(int i = 1; i <= Tmax; i++) {
        	out.write(i + "  " + taskPosition[i] + "  "+ tcost[i] + " " + tfail[i] + "\r\n"); 
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
