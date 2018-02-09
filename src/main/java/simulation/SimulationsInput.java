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
		String pathname = "D:\\Task.txt";
		File filename = new File(pathname);
		InputStreamReader reader = new InputStreamReader(  
                new FileInputStream(filename)); // 建立一个输入流对象reader  
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言  
        String line = "";  
        line = br.readLine();  
        int i = 0;
        
        Task tsel = new Task();
        while (line != null) {  
            String[] a = line.trim().split("\t");
        	tsel.setRest(Integer.parseInt(a[1]));
            tsel.setK(Integer.parseInt(a[2]));
            tsel.setWl(Double.parseDouble(a[3]));
            tsel.setIp(Double.parseDouble(a[4]));
            tsel.setOp(Double.parseDouble(a[5]));
            
        	
            task.add(tsel);
        	
        	i++;
        	line = br.readLine(); // 一次读入一行数据  
        }
        
        
		 } catch (Exception e) {  
             e.printStackTrace();  
         }
		 
		 Simulation.DiaoDu(Result.load(), 0.5, task, 1);

	}
}
