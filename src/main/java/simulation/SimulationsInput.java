package simulation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import model.Result;
import model.Task;

public class SimulationsInput {
	public static void simulationStart() {
		List<Task> task = new ArrayList<Task>(); 
		
		try {
		String pathname = "./Task.txt";
		File filename = new File(pathname);
		InputStreamReader reader = new InputStreamReader(  
                new FileInputStream(filename)); // 建立一个输入流对象reader  
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言  
        String line = "";  
        line = br.readLine();  
  
        int k = Integer.parseInt(line.trim());
        line = br.readLine();  
        
        while (line != null) {  
            String[] a = line.trim().split("\t");
            Task tsel = new Task();
            
            tsel.setWl(Double.parseDouble(a[1]));
            tsel.setIp(Double.parseDouble(a[2]));
            tsel.setOp(Double.parseDouble(a[3]));
            tsel.setRest(Integer.parseInt(a[4]));
            tsel.setK(k);
            
            task.add(tsel);
        	
      
        	line = br.readLine(); // 一次读入一行数据  
        }
        
        
		 } catch (Exception e) {  
             e.printStackTrace();  
         }
		 
		 Simulation.DiaoDu(Result.load(), Parameter.beita, task, 0);

	}
}
