package simulation;

import java.util.List;
import java.util.Map;
import java.util.Random;

import model.State;
import model.Task;
import param.AdHocParam;
import param.Argument;
import param.CloudletParam;
import param.LocalParam;
import property.TrainingTable;
import reward.Reward;
import reward.RewardBackValue;
import reward.Strategy;
import reward.offline.RewardOFL;
import reward.offline.StrategyOFL;

public class Greedy {
	public void greedy(Map<Long, Map<Strategy,Double>> map, double fsc, double beita, List<Task> task, int x, int n) {
		List<Task> q = null; //任务队列
		List<Task> e = null; //中转队列
		int el = 0;//中转队列长度
		Task tsel = null; //临时任务变量
		State state = null;
		RewardBackValue rsa = null;
		double fsch = 0;
		double totalfsch = 0;
		int a = 0;
		int Tmax = task.size();
		int[] per = new int[3];
		int[] taskPosition = new int[Tmax+1];
		Parameter para = new Parameter();
		
		//构造服从指数分布的时间间隔序列Tn 
		double lamda = 2, t = 0;
		Strategy str = null;
		
		int i = 1, rw = 1;//当前决策任务标号和到达任务的标号
		double [] t1 = new double[Tmax+1];
		double [] t2 = new double [Tmax+1];
		double [] tcost = new double[Tmax+1];
		int[] tw = new int[Tmax+1];
		
		t1[0] = 0;
		//存入t1为每个任务的到达时间从下标1开始
		while(i <= Tmax) {
			t1[i] = RandExp(para.lamdaq) + t1[i - 1];
			i++;
		}
		i = 1;
	
		q.add(task.get(0));	//将第一个任务直接开始存入Q进行决策
		tw[1] = 1;
		
		while(!q.isEmpty()) {
			rw++;
			//存入e中中转
			while(t1[rw] < t) {
				e.add(task.get(rw));
				tw[rw] = q.size() + e.size();//等待队列长度
				rw++;
			}
			//是否需要排序，然后加入q队列，有问题需修改
			if(!e.isEmpty()) {
				if(el > 1) {
					//e = sortTask(e,el);
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
			
			//环境模拟
			Environment envi = enviroSimulation(para);
			calculateTimes(para, tsel);
			
			//更新state
			state = new State(envi.Z, getPossionVariable(para.lamdan), envi.N, tw[i]);
			
			//开始决策
			tsel = q.get(0); //7
			
			
			if(x == 0) //在线 
			{			
				switch(n) {
				case 0://reward
					str = SelectAction(map, state, tsel);
					break;
				case 1: //fsch
					str = SelectAction2(map, state, tsel);
				}
			}else {
				switch(n) //离线
				{
				case 0://reward
					str = SelectAction3(map, state, tsel);
					break;
				case 1: //fsch
					str = changeAction(state, tsel);
				}
			}
				
										
			t2[i] = runtime(i,a, state, tsel);
			//删除，自增计数
			q.remove(tsel);//17
			a = getStrategy(str);
			per[a]++; //统计
			taskPosition[i] = a; 
						
			//下次决策时间
			t = t1[i] + t2[i];
			totalfsch += fsch;
			tcost[i] = rsa.getCost();
			
			i++;
		}
		
		SimulationOut.output(per, totalfsch, taskPosition, tcost, Tmax);
		return;
		
		
		
		
			
	}
	//学习
		private static Strategy learningA(State state) {
			Strategy str = null; 
			
			switch (TrainingTable.algorithm){
	         case 0: //贪心算法
	        	 str = Learning2.greedy.trainState(state.getStateID());
	             break;
	         case 1: //softmax算法
	        	 str = Learning2.softmax.trainState(state.getStateID());
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
			Environment envi = null;
			envi.Z = 0;
			int n; //相遇的节点数
			double[] p	= new double[100];
			envi.Z = calculateZ(envi.Z, para.p);
			
			for(double i = 0; i < 100; i++) {
				p[(int) i] = Math.pow(Math.E, -1.5*Math.pow(3, 0.5)*Math.pow(para.a,2))*Math.pow(1.5*Math.pow(3, 0.5)*Math.pow(para.a,2)*para.lamdac, i)/ getNFactorial((long)i);
			}
			envi.N = selectN(p,99); //计算N 
					
			//envi.times = calculateTimes(para);
			
			return envi;
			
		}
		//模拟执行次数
		private static void calculateTimes(Parameter para, Task task) {
			Random rand = new Random();
			int t = 1;
			int tu = 1;
			int to = 1;
			int td = 1;
			switch(para.str) {
			case Local:
				while(true) {
					if(rand.nextDouble()> para.fl)
						break;
					t++;
					if(t == task.getK()) break;
				}
				OperatingTimes.t = t;
			case Cloudlet:
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
				OperatingTimes.t = t;
				OperatingTimes.tu = tu;
				OperatingTimes.td = td;
			case AdHoc:
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
				OperatingTimes.t = t;
				OperatingTimes.tu = tu;
				OperatingTimes.to = to;
				OperatingTimes.td = td;
			}
			
		}
		//计算用户区域
		private static int calculateZ(int z, double p) {
			Random rand = new Random();
			double r = rand.nextDouble();
			if(r > p)
				return z;
			
			for(int i = 1; i < 20; i++) {
				if(r <= p * (double)i / 19.0) {
					z = z + i;
					break;
				}
			}
			
			if(z > 19) z = z - 20;
			
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
		private static int selectN(double[] p, int N) {
			 double m = 0;
			 Random r = new Random(); //r为0至1的随机数
			 double t = r.nextDouble();
			 for (int i = 0; i <= N; i++) {
			    /**
			     * 产生的随机数在m~m+P[i]间则认为选中了i，因此i被选中的概率是P[i]
			     */
			        m = m + p[i];
			        if (t <= m) return i;
			    }
			return 0;
		}
		//选择Strategy
		private static Strategy SelectAction(Map<Long, Map<Strategy, Double>> map, State state, Task tsel) {
			//根据格式遍历搜索
			double min = 99999999;
			Strategy str = Strategy.Local;
			
			for (Long in : map.keySet()) {
				if(in == state.getStateID()) {
					Map<Strategy, Double> map2 = map.get(in);
					for(Strategy j : map2.keySet()) {
						if(map2.get(j) < min) {
							str = j;
							min = map2.get(j);
						}
					}
				}
			}
			return str;
		}
		private static Strategy SelectAction2(Map<Long, Map<Strategy, Double>> map, State state, Task tsel) {
			//根据格式遍历搜索
			double min, t;
			Strategy stra = Strategy.Local;
			Strategy str = Strategy.Local;
			min = Reward.getReward(state, str, tsel).getFailureRate()*LocalParam.cPen;
			
			str = Strategy.Cloudlet;
			t = Reward.getReward(state, str, tsel).getFailureRate()*CloudletParam.cPen;
			if(t<min) {
				min = t;
				stra = str;
			}
			
			str = Strategy.AdHoc;
			t = Reward.getReward(state, str, tsel).getFailureRate();
			if(t<min) {
				min = t;
				stra = str;
			}
			
			return stra;
		}
		private static Strategy SelectAction3(Map<Long, Map<Strategy, Double>> map, State state, Task tsel) {
			//根据格式遍历搜索
			double min, t;
			Strategy stra = Strategy.Local;
			StrategyOFL str = StrategyOFL.Local;
			min = RewardOFL.getReward(state, str, tsel).getCost();
			
			str = StrategyOFL.Cloudlet;
			t = RewardOFL.getReward(state, str, tsel).getCost();
			if(t<min) {
				min = t;
				stra = Strategy.Cloudlet;
			}
			
			str = StrategyOFL.AdHoc;
			t = RewardOFL.getReward(state, str, tsel).getCost();
			if(t<min) {
				min = t;
				stra = Strategy.AdHoc;
			}
			
			return stra;
		}
		
		
		//计算运行时间
		private static double runtime(int i, int a, State state, Task task) {
			double time = 0;
			double delta=1.0/state.getN();
			switch(a) {
			case 0:
				time = task.getWl()/Argument.sCpu * OperatingTimes.t;
				break;
			case 1:
				CloudletParam.delta=delta;
				time = task.getIp()/CloudletParam.rUp*delta * OperatingTimes.tu 
						+ task.getWl()/CloudletParam.sCloudlet * OperatingTimes.td
						+ task.getOp()/CloudletParam.rDown*delta * OperatingTimes.td;
				break;
			case 2:
				AdHocParam.delta=delta;
				time = FSCH.calculateTime(task,new AdHocParam());
			}
			return time;
		}
		//根据F选择Strategy
		private static Strategy changeAction(State state, Task tsel) {
			double delta=1.0/state.getN();//计算拥塞程度 
			double a =  FSCH.calculateFsch0(tsel,new LocalParam());
			CloudletParam.delta=delta;
			double b =  FSCH.calculateFsch1(tsel,new CloudletParam());
			AdHocParam.delta=delta;
			double c =  FSCH.calculateFsch2(tsel,new AdHocParam());
			if(a < b)
				if(a < c)
					return Strategy.Local;
				else
					return Strategy.AdHoc;
			else
				if(b < c)
					return Strategy.Cloudlet;
				else
					return Strategy.AdHoc;		   
		}
		//排序，已经不用了
		private static List<Task> sortTask(List<Task> e, int el) {
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