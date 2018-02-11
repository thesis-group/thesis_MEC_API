package simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import model.*;
import param.*;
import property.TrainingTable;
import reward.*;
import reward.offline.*;
import rl.Learning;
import tools.ActionFilter;

public class Simulation {
	public static void DiaoDu(Map<Long, Map<Strategy,Double>> map, double beita, List<Task> task, int n) {
		
		int Tmax = task.size();
		double [] t1 = new double[Tmax +2];
		Parameter para = new Parameter();
		
		//构造服从指数分布的时间间隔序列Tn 		
		int i = 1;			
		t1[0] = 0;
		//存入t1为每个任务的到达时间从下标1开始
		while(i <= Tmax) {
			t1[i] = RandExp(para.lamdaq) + t1[i - 1];
			i++;
		}	
		//环境模拟		
		List<Environment> environment = new ArrayList<>();
		for(int j = 0; j < Tmax; j++) {
			Environment e = new Environment();
			e = enviroSimulation(para);
			environment.add(e);
		}
		
		//调度算法
		initialcount();
		FtOPAlgorithm(map,beita,task,environment,t1,para);
		GreedyAlgorithm(map,beita,task,environment,t1,para,0,0);
		GreedyAlgorithm(map,beita,task,environment,t1,para,0,1);
		GreedyAlgorithm(map,beita,task,environment,t1,para,1,0);
		GreedyAlgorithm(map,beita,task,environment,t1,para,1,1);
		RandomAlgorithm(map,beita,task,environment,t1,para);
	}
	
	private static void GreedyAlgorithm(Map<Long, Map<Strategy, Double>> map, double beita, List<Task> task, List<Environment> environment, double[] t1, Parameter para, int x, int n) {
		int a = 0, nodo = 0;
		double t = t1[1];
		int Tmax = task.size();
		int[] per = new int[3];
		int[] taskPosition = new int[Tmax+1];
		int i = 1, rw = 1;//当前决策任务标号和到达任务的标号
		double [] t2 = new double [Tmax+2];
		double [] tcost = new double[Tmax+1];
		double [] tfail = new double[Tmax+1];
		int[] tw = new int[Tmax+1];		
		List<Task> q = new ArrayList<Task>(); //任务队列
		List<Task> e = new ArrayList<Task>(); //中转队列
		int el = 0;//中转队列长度
		Task tsel = new Task(); //临时任务变量
		State state = new State();
		RewardBackValue rsa = new RewardBackValue(0,0,0);
		double fsch = 0;
		double totalfsch = 0, totalcost = 0;
		double cost = 0;
		Strategy str = null;
		q.add(task.get(0));	//将第一个任务直接开始存入Q进行决策
		tw[1] = 1;
		rw++;
		
		while(i<=Tmax) {
			//存入e中中转
			while(t1[rw] < t&&t1[rw]!=0) {
				Task tt = new Task(task.get(rw-1).getRest(),task.get(rw-1).getK(),task.get(rw-1).getWl(),task.get(rw-1).getIp(),task.get(rw-1).getOp(),task.get(rw-1).getWait()); 
				e.add(tt);
				el++;
				tw[rw] = q.size() + e.size();//等待队列长度
				rw++;
			}
			//是否需要排序，然后加入q队列，有问题需修改
			if(!e.isEmpty()) {
				while(el != 0) {
					q.add(e.get(0));
					e.remove(0);
					el--;
				}
			}
			
			//环境模拟载入
			Environment envi = environment.get(i-1);
			
			if(!q.isEmpty()) {
				tsel = q.get(0); //7
			}else {
				t = t + 0.01;
				continue;
			}
			if(tsel.getRest()-(t-t1[i]) <= 0) {
				q.remove(0);
				i++;
				nodo++;
				continue;
			}
			tsel.setWait(t-t1[i]);
			tsel.setRest(tsel.getRest()-tsel.getWait());
			
			//执行次数模拟
			calculateTimes(para, tsel);
			
			//更新state
			state = new State(envi.Z, envi.N, getPossionVariable(para.lamdan), tw[i]);
			
			//开始决策
			
			if(x == 0) //在线 
			{			
				switch(n) {
				case 0://reward
					str = SelectAction1(state, tsel);
					break;
				case 1: //fsch
					str = SelectAction2(state, tsel);
				}
			}else {
				switch(n) //离线
				{
				case 0://reward
					str = SelectAction3(state, tsel);
					break;
				case 1: //fsch
					str = changeAction(state, tsel);
				}
			}
			
			
			fsch = calculateFsche(state, str, tsel); //9
			
			rsa = rewardR(state, str, tsel, 1); //11
			

			a = getStrategy(str);
			t2[i] = runtime(i,a, state, tsel);
			//删除，自增计数
			q.remove(0);//17
			
			per[a]++; //统计
			taskPosition[i] = a; 
						
			//下次决策时间
			t = t + t2[i];
			totalfsch += fsch;
			
			tcost[i] = rsa.getCost();
			totalcost += tcost[i];
			tfail[i] = fsch;
			i++;
		}
		String name = "Result-Greedy-" + x + "-" + n + ".txt";
		SimulationOut.output(per, totalfsch,totalcost, taskPosition, tcost, tfail, Tmax, name, nodo);
		
	}

	private static void FtOPAlgorithm(Map<Long, Map<Strategy, Double>> map, double beita, List<Task> task, List<Environment> environment, double[] t1, Parameter para) {
		int a = 0, nodo = 0;
		double t = t1[1];
		int Tmax = task.size();
		int[] per = new int[3];
		int[] taskPosition = new int[Tmax+1];
		int i = 1, rw = 1;//当前决策任务标号和到达任务的标号
		double [] t2 = new double [Tmax+2];
		double [] tcost = new double[Tmax+1];
		double [] tfail = new double[Tmax+1];
		int[] tw = new int[Tmax+1];		
		List<Task> q = new ArrayList<Task>(); //任务队列
		List<Task> e = new ArrayList<Task>(); //中转队列
		int el = 0;//中转队列长度
		Task tsel = new Task(); //临时任务变量
		State state = new State();
		RewardBackValue rsa = new RewardBackValue(0,0,0);
		double fsch = 0;
		double totalfsch = 0, totalcost = 0;
		double cost = 0;
		Strategy str = null;
		q.add(task.get(0));	//将第一个任务直接开始存入Q进行决策
		tw[1] = 1;
		rw++;
		
		while(i<=Tmax) {
			//存入e中中转
			while(t1[rw] < t&&t1[rw]!=0) {
				Task tt = new Task(task.get(rw-1).getRest(),task.get(rw-1).getK(),task.get(rw-1).getWl(),task.get(rw-1).getIp(),task.get(rw-1).getOp(),task.get(rw-1).getWait()); 
				e.add(tt);
				el++;
				tw[rw] = q.size() + e.size();//等待队列长度
				rw++;
			}
			//是否需要排序，然后加入q队列，有问题需修改
			if(!e.isEmpty()) {
				while(el != 0) {
					q.add(e.get(0));
					e.remove(0);
					el--;
				}
			}
			
			//环境模拟载入
			Environment envi = environment.get(i-1);
			
			if(!q.isEmpty()) {
				tsel = q.get(0); //7
			}else {
				t = t + 0.01;
				continue;
			}
			if(tsel.getRest()-(t-t1[i]) <= 0) {
				q.remove(0);
				i++;
				nodo++;
				continue;
			}
			tsel.setWait(t-t1[i]);
			tsel.setRest(tsel.getRest()-tsel.getWait());
			
			//执行次数模拟
			calculateTimes(para, tsel);
			
			//更新state
			state = new State(envi.Z, envi.N, getPossionVariable(para.lamdan), tw[i]);
			
			//开始决策
			
			str = learningA(map, state); //10
			
			fsch = calculateFsche(state, str, tsel); //9
			
			rsa = rewardR(state, str, tsel, 1); //11
			
			cost = map.get(state.getStateID()).get(str);
			
			int count = Learning2Para.count.get(state.getStateID()).get(str);
			double newcost = (cost*count + rsa.getCost())/(count+1);
			
			Map<Strategy, Integer> countnum = Learning2Para.count.get(state.getStateID());
			countnum.put(str,count+1);
            Learning2Para.count.put(state.getStateID(),countnum);
						
			Map<Strategy,Double> mapp = map.get(state.getStateID());
			mapp.put(str, newcost);
			map.put(state.getStateID(), mapp);
			
			
			if (fsch > beita) {
				str = changeAction(state, tsel); //13-16.
				rsa = rewardR(state, str, tsel, 1);
				fsch = calculateFsche(state, str, tsel); 
			}
			
			a = getStrategy(str);
			t2[i] = runtime(i,a, state, tsel);
			//删除，自增计数
			q.remove(0);//17
			
			per[a]++; //统计
			taskPosition[i] = a; 
						
			//下次决策时间
			t = t + t2[i];
			totalfsch += fsch;
			
			tcost[i] = rsa.getCost();
			totalcost += tcost[i];
			tfail[i] = fsch;
			i++;
		}
		String name = "Result-FtOP.txt";
		SimulationOut.output(per, totalfsch,totalcost, taskPosition, tcost, tfail, Tmax, name, nodo);
		
	}
	
	private static void RandomAlgorithm(Map<Long, Map<Strategy, Double>> map, double beita, List<Task> task, List<Environment> environment, double[] t1, Parameter para) {
		int a = 0, nodo = 0;
		double t = t1[1];
		int Tmax = task.size();
		int[] per = new int[3];
		int[] taskPosition = new int[Tmax+1];
		int i = 1, rw = 1;//当前决策任务标号和到达任务的标号
		double [] t2 = new double [Tmax+2];
		double [] tcost = new double[Tmax+1];
		double [] tfail = new double[Tmax+1];
		int[] tw = new int[Tmax+1];		
		List<Task> q = new ArrayList<Task>(); //任务队列
		List<Task> e = new ArrayList<Task>(); //中转队列
		int el = 0;//中转队列长度
		Task tsel = new Task(); //临时任务变量
		State state = new State();
		RewardBackValue rsa = new RewardBackValue(0,0,0);
		double fsch = 0;
		double totalfsch = 0, totalcost = 0;
		double cost = 0;
		Strategy str = null;
		q.add(task.get(0));	//将第一个任务直接开始存入Q进行决策
		tw[1] = 1;
		rw++;
		
		while(i<=Tmax) {
			//存入e中中转
			while(t1[rw] < t&&t1[rw]!=0) {
				Task tt = new Task(task.get(rw-1).getRest(),task.get(rw-1).getK(),task.get(rw-1).getWl(),task.get(rw-1).getIp(),task.get(rw-1).getOp(),task.get(rw-1).getWait()); 
				e.add(tt);
				el++;
				tw[rw] = q.size() + e.size();//等待队列长度
				rw++;
			}
			//是否需要排序，然后加入q队列，有问题需修改
			if(!e.isEmpty()) {
				while(el != 0) {
					q.add(e.get(0));
					e.remove(0);
					el--;
				}
			}
			
			//环境模拟载入
			Environment envi = environment.get(i-1);
			
			if(!q.isEmpty()) {
				tsel = q.get(0); //7
			}else {
				t = t + 0.01;
				continue;
			}
			if(tsel.getRest()-(t-t1[i]) <= 0) {
				q.remove(0);
				i++;
				nodo++;
				continue;
			}
			tsel.setWait(t-t1[i]);
			tsel.setRest(tsel.getRest()-tsel.getWait());
			
			//执行次数模拟
			calculateTimes(para, tsel);
			
			//更新state
			state = new State(envi.Z, envi.N, getPossionVariable(para.lamdan), tw[i]);
			
			//开始决策
			
			List<Strategy> possibleAction =ActionFilter.getPossibleAction(state);
			
			str = selectRandomAction(possibleAction);
			
			fsch = calculateFsche(state, str, tsel); //9
			
			rsa = rewardR(state, str, tsel, 1); //11
			
			a = getStrategy(str);
			t2[i] = runtime(i,a, state, tsel);
			//删除，自增计数
			q.remove(0);//17
			
			per[a]++; //统计
			taskPosition[i] = a; 
						
			//下次决策时间
			t = t + t2[i];
			totalfsch += fsch;
			
			tcost[i] = rsa.getCost();
			totalcost += tcost[i];
			tfail[i] = fsch;
			i++;
		}
		String name = "Result-Random.txt";
		SimulationOut.output(per, totalfsch,totalcost, taskPosition, tcost, tfail, Tmax, name, nodo);
		
	}
	
	private static Strategy selectRandomAction(List<Strategy> possibleAction) {
		double y = Math.random();
		if(y<1/(double)possibleAction.size())
			return possibleAction.get(0);
		else if(y<2/(double)possibleAction.size())
			return possibleAction.get(1);
			else return possibleAction.get(2);
	}

	private static void initialcount() {
		for(Long i = 0l; i < StateParam.Zmax*(StateParam.Nmax-StateParam.Nmin)* StateParam.Vmax*StateParam.Qmax ; i++) {
			Map<Strategy, Integer> countnum = new HashMap<>();
			countnum.put(Strategy.Local, TrainParam.iter);
			countnum.put(Strategy.Cloudlet, TrainParam.iter);
			countnum.put(Strategy.AdHoc, TrainParam.iter);
			Learning2Para.count.put(i,countnum);
		}
		
	}

	//学习
	private static Strategy learningA(Map<Long, Map<Strategy, Double>> map, State state) {
		Strategy str = null; 
		
		switch (TrainingTable.algorithm){
         case 0: //贪心算法
        	 str = Learning2.greedy.trainState(map, state.getStateID());
             break;
         case 1: //softmax算法
        	 str = Learning2.softmax.trainState(map, state.getStateID());
             break;
     }
		return str;
	}
	//计算Fsche
	private static double calculateFsche(State state, Strategy str, Task tsel) {
		double delta=1.0/state.getN();//计算拥塞程度

		switch(str) {
		case Local: 
			return FSCH.calculateFsch0(tsel,new LocalParam());
		case Cloudlet:
			CloudletParam.delta=delta;
			return FSCH.calculateFsch1(tsel,new CloudletParam());
		case AdHoc:
			AdHocParam.delta=delta;
			return FSCH.calculateFsch2(tsel,new AdHocParam());
		}
		return 0;
	}
	//计算reward
	private static RewardBackValue rewardR(State state, Strategy str, Task tsel, int n) {
		switch(n) {
		case 0://在线
			return Reward.getReward(state, str, tsel);
		case 1://离线
			StrategyOFL strr = null;
			switch(str) {
			case Local:
				strr = StrategyOFL.Local;
				break;
			case Cloudlet:
				strr = StrategyOFL.Cloudlet;
				break;
			case AdHoc:
				strr = StrategyOFL.AdHoc;
			}
			return RewardOFL.getReward(state, strr, tsel);
		}
		return null;
	}
	//部分环境模拟
	private static Environment enviroSimulation(Parameter para) {
		Environment envi = new Environment();
		envi.Z = Environment.NZ;
		int n; //相遇的节点数
		double[] p	= new double[100];
		envi.Z = calculateZ(envi.Z, para.p);
		Environment.NZ = envi.Z;
		
		for(double i = 0; i < StateParam.Nmax - StateParam.Nmin; i++) {
			p[(int) i] = Math.pow(Math.E, -1.5*Math.pow(3, 0.5)*Math.pow(para.a,2))*Math.pow(1.5*Math.pow(3, 0.5)*Math.pow(para.a,2)*para.lamdac, i)/ getNFactorial((long)i);
		}
		envi.N = selectN(p); //计算N 
		
		return envi;
		
	}
	//模拟执行次数
	private static void calculateTimes(Parameter para, Task task) {
		Random rand = new Random();
		int t = 1;
		int tu = 1;
		int to = 1;
		int td = 1;
	
			while(true) {
				if(rand.nextDouble()> para.fl)
					break;
				t++;
				if(t == task.getK()) break;
			}
			OperatingTimes.tL = t;
		
			while(true) {
				if(rand.nextDouble() > para.fup)
					if(rand.nextDouble() > para.fdown)
						break;
					else {
						tu++;
						td++;
					}
				else tu++;
				t++;
				if(t == task.getK()) break;
			}
			OperatingTimes.tC = t;
			OperatingTimes.tuC = tu;
			OperatingTimes.tdC = td;
		
			while(true) {
				if(rand.nextDouble() > para.ft)
					if(rand.nextDouble() > para.fad)
						if(rand.nextDouble() > para.ft)
							break;
						else {
							tu++;
							to++;
							td++;
						}
					else {
						tu++;
						to++;
					}
				else {
					tu++;
				}
				t++;
				if(t == task.getK()) break;	
			}
			OperatingTimes.tA = t;
			OperatingTimes.tuA = tu;
			OperatingTimes.toA = to;
			OperatingTimes.tdA = td;
		
		
	}
	//计算用户区域
	private static int calculateZ(int z, double p) {
		Random rand = new Random();
		double r = rand.nextDouble();
		if(r > p)
			return z;
		
		for(int i = 1; i < StateParam.Zmax; i++) {
			if(r <= p * (double)i / (StateParam.Zmax - 1)) {
				z = z + i;
				break;
			}
		}
		
		if(z > StateParam.Zmax - 1) z = z - StateParam.Zmax;
		
		return z;
	}
	//迭代阶乘
	public static long getNFactorial(long n){
        if(n==0){
            return 1;
        }
        return n*getNFactorial(n-1);
    }
	//选择N的大小
	private static int selectN(double[] p) {
		 double m = 0;
		 Random r = new Random(); //r为0至1的随机数
		 double sum = 0;
		 for(int i = 0; i<p.length;i++)
			 sum += p[i];
		 
		 
		 double t = r.nextDouble() * sum;
		 for (int i = 0; i < StateParam.Nmax - StateParam.Nmin; i++) {
		    /**
		     * 产生的随机数在m~m+P[i]间则认为选中了i，因此i被选中的概率是P[i]
		     */
		        m = m + p[i];
		        if (t <= m) return i+StateParam.Nmin;
		    }
		return StateParam.Nmin;
	}
	//选择Strategy
	private static Strategy SelectAction(Map<Long, Map<Strategy, Double>> map, State state, Task tsel) {
		//根据格式遍历搜索
		double min = Double.POSITIVE_INFINITY;
		Strategy str = Strategy.Local;
		
		long in = state.getStateID();
		Map<Strategy, Double> map2 = map.get(in);
		for(Strategy j : map2.keySet()) {
			if(map2.get(j) < min) {
				str = j;
				min = map2.get(j);
			}
		}
		return str;
	}
	//计算运行时间
	private static double runtime(int i, int a, State state, Task task) {
		double time = 0;
		double delta=1.0/state.getN();
		switch(a) {
		case 0:
			time = task.getWl()/Argument.sCpu * OperatingTimes.tL;
			break;
		case 1:
			CloudletParam.delta=delta;
			time = task.getIp()/CloudletParam.rUp*delta * OperatingTimes.tuC 
					+ task.getWl()/CloudletParam.sCloudlet * OperatingTimes.tdC
					+ task.getOp()/CloudletParam.rDown*delta * OperatingTimes.tdC;
			break;
		case 2:
			AdHocParam.delta=delta;
			time = FSCH.calculateTime(task,new AdHocParam());
		}
		return time;
	}
	//根据F选择Strategy
	private static Strategy changeAction(State state, Task tsel) {
		Strategy str = Strategy.Local;
		List<Strategy> possibleAction =ActionFilter.getPossibleAction(state);
		Map<Strategy, Double> map = new HashMap<Strategy, Double>();
		double min = Double.POSITIVE_INFINITY;
		double delta=1.0/state.getN();//计算拥塞程度 
		map.put(Strategy.Local, FSCH.calculateFsch0(tsel,new LocalParam()));
		CloudletParam.delta=delta;
		map.put(Strategy.Cloudlet, FSCH.calculateFsch1(tsel,new CloudletParam()));
		AdHocParam.delta=delta;
		map.put(Strategy.AdHoc, FSCH.calculateFsch2(tsel,new AdHocParam()));
		
		for(Strategy j : possibleAction) {
			if(map.get(j) < min) {
				str = j;
				min = map.get(j);
			}
		}
		return str;   
	}

	private static Strategy SelectAction1(State state, Task tsel) {
	
		List<Strategy> possibleAction =ActionFilter.getPossibleAction(state);
		
		double min, t;
		Strategy stra = Strategy.Local;
		Strategy str = Strategy.Local;
		min = Reward.getReward(state, str, tsel).getCost();
		
		str = Strategy.Cloudlet;
		if(possibleAction.contains(str)) {
			t = Reward.getReward(state, str, tsel).getCost();
		if(t<min) {
			min = t;
			stra = str;
		}
		}
		
		str = Strategy.AdHoc;
		if(possibleAction.contains(str)) {
		t = Reward.getReward(state, str, tsel).getCost();
		if(t<min) {
			min = t;
			stra = str;
		}
		}
		return stra;
	}
	
	private static Strategy SelectAction2(State state, Task tsel) {
		List<Strategy> possibleAction =ActionFilter.getPossibleAction(state);
		double min, t;
		Strategy stra = Strategy.Local;
		Strategy str = Strategy.Local;
		min = Reward.getReward(state, str, tsel).getFailureRate()*LocalParam.cPen;
		
		
		str = Strategy.Cloudlet;
		if(possibleAction.contains(str)) {
		t = Reward.getReward(state, str, tsel).getFailureRate()*CloudletParam.cPen;
		if(t<min) {
			min = t;
			stra = str;
		}
		}
		
		str = Strategy.AdHoc;
		if(possibleAction.contains(str)) {
		t = Reward.getReward(state, str, tsel).getFailureRate();
		if(t<min) {
			min = t;
			stra = str;
		}
		}
		return stra;
	}
	
	private static Strategy SelectAction3(State state, Task tsel) {
		List<Strategy> possibleAction =ActionFilter.getPossibleAction(state);
		double min, t;
		Strategy stra = Strategy.Local;
		StrategyOFL str = StrategyOFL.Local;
		min = RewardOFL.getReward(state, str, tsel).getCost();
		
		str = StrategyOFL.Cloudlet;
		if(possibleAction.contains(Strategy.Cloudlet)) {
		t = RewardOFL.getReward(state, str, tsel).getCost();
		if(t<min) {
			min = t;
			stra = Strategy.Cloudlet;
		}
		}
		
		str = StrategyOFL.AdHoc;
		if(possibleAction.contains(Strategy.AdHoc)) {
		t = RewardOFL.getReward(state, str, tsel).getCost();
		if(t<min) {
			min = t;
			stra = Strategy.AdHoc;
		}
		}
		
		return stra;
	}
	//泊松过程模拟
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
	//泊松随机
	private static int getPossionVariable(double lamda) {  
        int x = 0;  
        double y = Math.random(), cdf = getPossionProbability(x, lamda);  
        while (cdf < y) {  
            x++;  
            cdf += getPossionProbability(x, lamda);  
        }  
        return x;  
    }  
	//概率
    private static double getPossionProbability(int k, double lamda) {  
        double c = Math.exp(-lamda), sum = 1;  
        for (int i = 1; i <= k; i++) {  
            sum *= lamda / i;  
        }  
        return sum * c;  
    }  
	//变Strategy为数字
    private static int getStrategy(Strategy str) {
		switch(str) {
		case Local:
			return 0;
		case Cloudlet:
			return 1;
		case AdHoc:
			return 2;
		}
		return 0;
	}
}

