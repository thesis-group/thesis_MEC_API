package simulation;

import java.io.*;
import java.util.List;

import model.Result;
import model.Task;

public class SimulationsInput {
	public static void simulationStart() {
		List<Task> task = null; 
		
		try {
		String pathname = "D:\\text.txt";
		File filename = new File(pathname);
		InputStreamReader reader = new InputStreamReader(  
                new FileInputStream(filename)); // 建立一个输入流对象reader  
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言  
        String line = "";  
        line = br.readLine();  
        int i = 0;
        
        Task tsel = null;
        while (line != null) {  
            
        	tsel.setRest(Integer.parseInt(line.substring(0, 0)));
            tsel.setK(Integer.parseInt(line.substring(0, 0)));
            tsel.setWl(Double.parseDouble(line.substring(0, 0)));
            tsel.setIp(Double.parseDouble(line.substring(0, 0)));
            tsel.setOp(Double.parseDouble(line.substring(0, 0)));
            tsel.setWait(Double.parseDouble(line.substring(0, 0)));
        	
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
