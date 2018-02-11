package simulation;

import java.io.*;

public class SimulationOut {
	
	
	public static void output(int[] per, double totalfsch, double totalcost, int[] taskPosition, double[] tcost, double[] tfail, int Tmax, String j, int nodo) {
		try {
			File writename = new File(j);
			
		
		writename.createNewFile(); // 创建新文件  
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));  
        for(int i = 1; i <= Tmax; i++) {
        	if(tcost[i] != 0)
        	out.write(i + "\t" + taskPosition[i] + "\t"+ tcost[i] + "\t" + tfail[i] + "\r\n"); 
        }
        out.write("本地 = "+ String.format("%.2f", per[0]/(double)(Tmax-nodo)*100) + "%\t云端 = "+ String.format("%.2f", per[1]/(double)(Tmax-nodo)*100) + "%\t自组织 = "+ String.format("%.2f", per[2]/(double)(Tmax-nodo)*100) + "%\r\n");
        out.write("执行 = "+ (Tmax-nodo) + "\t未执行 = "+ nodo + "\t总计 = "+ Tmax +"\r\n");
        out.write("totalfsch = "+ totalfsch + "\ttotalcost = "+ totalcost +"\r\n"); // \r\n即为换行  
        out.write("Averagefsch = "+ totalfsch/(double)(Tmax-nodo) + "\tAveragecost = "+ totalcost/(double)(Tmax-nodo)*100 +"\r\n"); 
        
        out.flush(); // 把缓存区内容压入文件  
        out.close(); // 最后记得关闭文件  
        
        
        
	 } catch (Exception e) {  
         e.printStackTrace();  
     }  
	}
}
