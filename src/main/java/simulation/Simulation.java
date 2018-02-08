package simulation;

import java.util.List;
import java.util.Map;
import java.util.Random;

import model.*;
import param.*;
import reward.*;
import reward.offline.*;
import rl.Learning;

public class Simulation {
	public static void DiaoDu(Map<Long, Map<Strategy,Double>> map, double beita, List<Task> task, int n) {
		List<Task> q = null; //�������
		List<Task> e = null; //��ת����
		int el = 0;//��ת���г���
		Task tsel = null; //��ʱ�������
		State state = null;
		double rsa = 0;
		double fsch = 0;
		double totalfsch = 0;
		int a = 0;
		int Tmax = task.size();
		int[] per = new int[3];
		int[] taskPosition = new int[Tmax+1];
		Parameter para = new Parameter();
		
		//�������ָ���ֲ���ʱ��������Tn 
		double lamda = 2, t = 0;
		Strategy str = null;
		
		int i = 1, rw = 1;//��ǰ���������ź͵�������ı��
		double [] t1 = new double[Tmax+1];
		double [] t2 = new double [Tmax+1];
		int[] tw = new int[Tmax+1];
		
		t1[0] = 0;
		//����t1Ϊÿ������ĵ���ʱ����±�1��ʼ
		while(i <= Tmax) {
			t1[i] = RandExp(para.lamdaq) + t1[i - 1];
			i++;
		}
		i = 1;
	
		q.add(task.get(0));	//����һ������ֱ�ӿ�ʼ����Q���о���
		tw[1] = 1;
		
		while(!q.isEmpty()) {
			rw++;
			//����e����ת
			while(t1[rw] < t) {
				e.add(task.get(rw));
				tw[rw] = q.size() + e.size();//�ȴ����г���
				rw++;
			}
			//�Ƿ���Ҫ����Ȼ�����q���У����������޸�
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
			
			//����ģ��
			Environment envi = enviroSimulation(para);
			calculateTimes(para, tsel);
			
			//����state
			state = new State(envi.Z, getPossionVariable(para.lamdan), envi.N, tw[i]);
			
			//��ʼ����
			tsel = q.get(0); //7
			
			str = SelectAction(map, state, tsel);	//8
			
			fsch = calculateFsche(state, str, tsel); //9
			
			rsa = rewardR(state, str, tsel, n).getCost(); //11
			
			Learning2.greedy.trainState(state.getStateID());
			str = null; //10
			
			if (fsch > beita)  
				str = changeAction(state, tsel); //13-16
			
			t2[i] = runtime(i,a, state, tsel);
			//ɾ������������
			q.remove(tsel);//17
			a = getStrategy(str);
			per[a]++; //ͳ��
			taskPosition[i] = a; 
			i++;			
			//�´ξ���ʱ��
			t = t1[i] + t2[i];
			totalfsch += fsch;
		}
		
		SimulationOut.output(per, totalfsch, taskPosition, Tmax);
		return;
	}

	private static double calculateFsche(State state, Strategy str, Task tsel) {
		double delta=1.0/state.getN();//����ӵ���̶�

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

	private static RewardBackValue rewardR(State state, Strategy str, Task tsel, int n) {
		switch(n) {
		case 0://����
			return Reward.getReward(state, str, tsel);
		case 1://����
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

	private static Environment enviroSimulation(Parameter para) {
		Environment envi = null;
		envi.Z = 0;
		int n; //�����Ľڵ���
		double[] p	= new double[100];
		envi.Z = calculateZ(envi.Z, para.p);
		
		for(double i = 0; i < 100; i++) {
			p[(int) i] = Math.pow(Math.E, -1.5*Math.pow(3, 0.5)*Math.pow(para.a,2))*Math.pow(1.5*Math.pow(3, 0.5)*Math.pow(para.a,2)*para.lamdac, i)/ getNFactorial((long)i);
		}
		envi.N = selectN(p,99); //����N 
				
		//envi.times = calculateTimes(para);
		
		return envi;
		
	}
	
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

	public static long getNFactorial(long n){
        if(n==0){
            return 1;
        }
        return n*getNFactorial(n-1);
    }
	
	private static int selectN(double[] p, int N) {
		 double m = 0;
		 Random r = new Random(); //rΪ0��1�������
		 double t = r.nextDouble();
		 for (int i = 0; i <= N; i++) {
		    /**
		     * �������������m~m+P[i]������Ϊѡ����i�����i��ѡ�еĸ�����P[i]
		     */
		        m = m + p[i];
		        if (t <= m) return i;
		    }
		return 0;
	}

	private static Strategy SelectAction(Map<Long, Map<Strategy, Double>> map, State state, Task tsel) {
		//���ݸ�ʽ��������
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

	private static double runtime(int i, int a, State state, Task task) {
		double time = 0;
		double delta=1.0/state.getN();
		switch(a) {
		case 0:
			time = task.getWl()/Argument.sCpu * OperatingTimes.t;
			break;
		case 1:
			CloudletParam.delta=delta;
			time = task.getIp()/CloudletParam.rUp*delta*OperatingTimes.tu 
					+ task.getWl()/CloudletParam.sCloudlet * OperatingTimes.td
					+ task.getOp()/CloudletParam.rDown*delta*OperatingTimes.td;
			break;
		case 2:
			AdHocParam.delta=delta;
			time = FSCH.calculateTime(task,new AdHocParam());
		}
		return time;
	}

	private static Strategy changeAction(State state, Task tsel) {
		double delta=1.0/state.getN();//����ӵ���̶� 
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
	
	private static double RandExp(double lamda) {
        Random rand = new Random();
        double p ;
        double temp;
        double randres;
        temp = 1.00 / lamda;
        
        while (true) {
        	//���ڲ���������ܶȣ���֤�Ȳ�����С����Ϊʱ��Ϊ��
            p = rand.nextDouble();
            if (p < lamda)
                break;
        }
        randres = -temp * Math.log(temp * p);
        return randres;
    }
	
	private static int getPossionVariable(double lamda) {  
        int x = 0;  
        double y = Math.random(), cdf = getPossionProbability(x, lamda);  
        while (cdf < y) {  
            x++;  
            cdf += getPossionProbability(x, lamda);  
        }  
        return x;  
    }  
  
    private static double getPossionProbability(int k, double lamda) {  
        double c = Math.exp(-lamda), sum = 1;  
        for (int i = 1; i <= k; i++) {  
            sum *= lamda / i;  
        }  
        return sum * c;  
    }  
	
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

