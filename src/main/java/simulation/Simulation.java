package simulation;

import java.util.List;
import java.util.Map;
import java.util.Random;

import model.*;
import param.*;

public class Simulation {
	public void DiaoDu(State state, double fsc, double beita, List<Task> task) {
		List<Task> q = null; //任务队列
		List<Task> e = null; //中转队列
		int el = 0;//中转队列长度
		Task tsel = null; //临时任务变量
		int a = 0;
		
		//构造服从指数分布的时间间隔序列Tn 
		double lamda = 2, t = 0;
		int Tmax = task.size();
		int i = 1, rw = 1;//当前决策任务标号和到达任务的标号
		double [] t1 = new double[Tmax];
		double [] t2 = new double [Tmax];
		t1[0] = 0;
		//存入t1为每个任务的到达时间从下标1开始
		while(i <= Tmax) {
			t1[i] = RandExp(lamda) + t1[i - 1];
			i++;
		}
		i = 1;
		q.add(task.get(0));	//将第一个任务直接开始存入Q进行决策
		
		while(!q.isEmpty()) {
			rw++;
			//存入e中中转
			while(t1[rw] < t) {
				e.add(task.get(rw));
				rw++;
			}
			//是否需要排序，然后加入q队列，有问题需修改
			if(!e.isEmpty()) {
				if(el > 1) {
					e = sortTask(e,el);
					while(el != 0) {
						q.add(e.get(0));
						e.remove(0);
						el--;
					}
				}
				q.add(e.get(0));
				e.remove(0);
				el--;
			}
			//开始决策
			tsel = q.get(0);
			a = SelectAction(state,tsel);
			t2[i] = runtime(i,a, state, tsel);
			//删除，自增计数
			q.remove(tsel);
			i++;
			//下次决策时间
			t = t1[i] + t2[i];
		}
		return;
	}

	private double runtime(int i, int a, State state, Task task) {
		double time = 0;
		double delta=1.0/state.getN();
		switch(a) {
		case 0:
			FSCH.calculateTime0(task,new LocalParam());
			break;
		case 1:
			CloudletParam.delta=delta;
			FSCH.calculateTime1(task,new CloudletParam());
			break;
		case 2:
			AdHocParam.delta=delta;
			FSCH.calculateTime2(task,new AdHocParam());
		}
		return time;
	}

	private int SelectAction(State state, Task task) {
		FSCH fsch = null;
		double delta=1.0/state.getN();//计算拥塞程度 
		double a =  FSCH.calculateFsch0(task,new LocalParam());
		CloudletParam.delta=delta;
		double b =  FSCH.calculateFsch1(task,new CloudletParam());
		AdHocParam.delta=delta;
		double c =  FSCH.calculateFsch2(task,new AdHocParam());
		if(a < b)
			if(a < c)
				return 0;
			else
				return 2;
		else
			if(b < c)
				return 1;
			else
				return 2;		   
	}

	private List<Task> sortTask(List<Task> e, int el) {
		Task t = null;
		int min = 0, m = 0;
		for(int i = 0; i < el; i++) {
			for(int j = i + 1; i < el; i++) {
				if(min > e.get(j).getRest()) {
					min = e.get(j).getRest();
					m = j;
				}
			}
			t = e.get(i);
			e.set(i,e.get(m));
			e.set(m,t);
		}
		return e;		
	}
	
	private static double RandExp(double lamda) {
        Random rand = new Random();
        double p ;
        double temp;
        double randres;
        temp = 1.00 / lamda;
        
        while (true) {
        	//用于产生随机的密度，保证比参数λ小，因为时间为正
            p = rand.nextDouble();
            if (p < lamda)
                break;
        }
        randres = -temp * Math.log(temp * p);
        return randres;
    }
	
	
}

