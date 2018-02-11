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
		List<Task> q = new ArrayList<Task>(); //�������
		List<Task> e = new ArrayList<Task>(); //��ת����
		int el = 0;//��ת���г���
		Task tsel = new Task(); //��ʱ�������
		State state = new State();
		RewardBackValue rsa = new RewardBackValue(0,0,0);
		double fsch = 0;
		double totalfsch = 0;
		double cost = 0;
		int a = 0;
		int Tmax = task.size();
		int[] per = new int[3];
		int[] taskPosition = new int[Tmax+1];
		Parameter para = new Parameter();
		
		//�������ָ���ֲ���ʱ��������Tn 
		double t = 0;
		Strategy str = null;
		
		int i = 1, rw = 1;//��ǰ���������ź͵�������ı��
		double [] t1 = new double[Tmax+2];
		double [] t2 = new double [Tmax+2];
		double [] tcost = new double[Tmax+1];
		double [] tfail = new double[Tmax+1];
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
		rw++;
		
		List<Environment> environment = new ArrayList<>();
		for(int j = 0; j < Tmax; j++) {
			environment.add(enviroSimulation(para));
		}
		
		//�����㷨
		initialcount();
		while(i<=Tmax) {
			//����e����ת
			while(t1[rw] < t&&t1[rw]!=0) {
				e.add(task.get(rw-1));
				el++;
				tw[rw] = q.size() + e.size();//�ȴ����г���
				rw++;
			}
			//�Ƿ���Ҫ����Ȼ�����q���У����������޸�
			if(!e.isEmpty()) {
				while(el != 0) {
					q.add(e.get(0));
					e.remove(0);
					el--;
				}
			}
			
			//����ģ������
			Environment envi = environment.get(i-1);
			
			tsel = q.get(0); //7
			
			//ִ�д���ģ��
			calculateTimes(para, tsel);
			
			//����state
			state = new State(envi.Z, envi.N, getPossionVariable(para.lamdan), tw[i]);
			
			//��ʼ����
			
			//str = SelectAction(map, state, tsel);	//8
			
			str = learningA(map, state); //10
			
			fsch = calculateFsche(state, str, tsel); //9
			
			rsa = rewardR(state, str, tsel, n); //11
			
			cost = map.get(state.getStateID()).get(str);
			
			int count = Learning2Para.count.get(state.getStateID()).get(str);
			double newcost = (cost*count + cost)/(count+1);
			
			Map<Strategy, Integer> countnum = Learning2Para.count.get(state.getStateID());
			countnum.put(str,count+1);
            Learning2Para.count.put(state.getStateID(),countnum);
						
			Map<Strategy,Double> mapp = map.get(state.getStateID());
			mapp.put(str, newcost);
			map.put(state.getStateID(), mapp);
			
			
			if (fsch > beita)  
				str = changeAction(state, tsel); //13-16
			
			a = getStrategy(str);
			t2[i] = runtime(i,a, state, tsel);
			//ɾ������������
			q.remove(tsel);//17
			
			per[a]++; //ͳ��
			taskPosition[i] = a; 
						
			//�´ξ���ʱ��
			t = t1[i] + t2[i];
			totalfsch += fsch;
			tcost[i] = rsa.getCost();
			tfail[i] = rsa.getFailureRate();
			i++;
		}
		
		SimulationOut.output(per, totalfsch, taskPosition, tcost, tfail, Tmax, 0);
		
		
		
		per = new int[3];
		totalfsch = 0;
		tcost = new double[Tmax+1];
		tfail = new double[Tmax+1];
				
		i = 1;
		t = 0;
		t1[0] = 0;
		q.add(task.get(0));	//����һ������ֱ�ӿ�ʼ����Q���о���
		tw[1] = 1;
		rw = 2;
		
		//greedy
		while(i<=Tmax) {
			//����e����ת
			while(t1[rw] < t&&t1[rw]!=0) {
				e.add(task.get(rw-1));
				el++;
				tw[rw] = q.size() + e.size();//�ȴ����г���
				rw++;
			}
			//�Ƿ���Ҫ����Ȼ�����q���У����������޸�
			if(!e.isEmpty()) {
				while(el != 0) {
					q.add(e.get(0));
					e.remove(0);
					el--;
				}
			}
			
			//����ģ������
			Environment envi = environment.get(i-1);
			//ִ�д���ģ��
			calculateTimes(para, tsel);
			
			//����state
			state = new State(envi.Z, envi.N, getPossionVariable(para.lamdan), tw[i]);
			
			//��ʼ����
			tsel = q.get(0); //7

			if(para.x == 0) //���� 
			{			
				switch(para.n) {
				case 0://reward
					str = SelectAction1(state, tsel);
					break;
				case 1: //fsch
					str = SelectAction2(state, tsel);
				}
			}else {
				switch(para.n) //����
				{
				case 0://reward
					str = SelectAction3(state, tsel);
					break;
				case 1: //fsch
					str = changeAction(state, tsel);
				}
			}
			fsch = calculateFsche(state, str, tsel);
			rsa = rewardR(state, str, tsel, n);	
			
			a = getStrategy(str);
			t2[i] = runtime(i,a, state, tsel);
			//ɾ������������
			q.remove(tsel);//17
			
			per[a]++; //ͳ��
			taskPosition[i] = a; 
						
			//�´ξ���ʱ��
			t = t1[i] + t2[i];
			totalfsch += fsch;
			tcost[i] = rsa.getCost();
			tfail[i] = rsa.getFailureRate();
			i++;
		}
		SimulationOut.output(per, totalfsch, taskPosition, tcost, tfail, Tmax, 1);
		
		
		per = new int[3];
		totalfsch = 0;
		tcost = new double[Tmax+1];
		tfail = new double[Tmax+1];
				
		i = 1;
		t = 0;
		t1[0] = 0;
		q.add(task.get(0));	//����һ������ֱ�ӿ�ʼ����Q���о���
		tw[1] = 1;
		rw = 2;
		
		//random
		while(i<=Tmax) {
		//����e����ת
			while(t1[rw] < t&&t1[rw]!=0) {
						e.add(task.get(rw-1));
						el++;
						tw[rw] = q.size() + e.size();//�ȴ����г���
						rw++;
					}
					//�Ƿ���Ҫ����Ȼ�����q���У����������޸�
					if(!e.isEmpty()) {
						while(el != 0) {
							q.add(e.get(0));
							e.remove(0);
							el--;
						}
					}
					
					//����ģ������
					Environment envi = environment.get(i-1);
					//ִ�д���ģ��
					calculateTimes(para, tsel);
					
					//����state
					state = new State(envi.Z,  envi.N, getPossionVariable(para.lamdan), tw[i]);
					
					//��ʼ����
					tsel = q.get(0); //7

					List<Strategy> possibleAction =ActionFilter.getPossibleAction(state);
					
					str = selectRandomAction(possibleAction);
					
					
					a = getStrategy(str);						
					t2[i] = runtime(i,a, state, tsel);
					//ɾ������������
					q.remove(tsel);//17
					
					fsch = calculateFsche(state, str, tsel);
					rsa = rewardR(state, str, tsel, n);	
					
					per[a]++; //ͳ��
					taskPosition[i] = a; 
								
					//�´ξ���ʱ��
					t = t1[i] + t2[i];
					totalfsch += fsch;
					tcost[i] = rsa.getCost();
					tfail[i] = rsa.getFailureRate();
					i++;
				}
				SimulationOut.output(per, totalfsch, taskPosition, tcost, tfail, Tmax, 2);
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
		for(Long i = 1l; i < (StateParam.Nmax-StateParam.Nmin)* StateParam.Vmax*StateParam.Qmax ; i++) {
			Map<Strategy, Integer> countnum = new HashMap<>();
			countnum.put(Strategy.Local, TrainParam.iter);
			countnum.put(Strategy.Cloudlet, TrainParam.iter);
			countnum.put(Strategy.AdHoc, TrainParam.iter);
			Learning2Para.count.put(i,countnum);
		}
		
	}

	//ѧϰ
	private static Strategy learningA(Map<Long, Map<Strategy, Double>> map, State state) {
		Strategy str = null; 
		
		switch (TrainingTable.algorithm){
         case 0: //̰���㷨
        	 str = Learning2.greedy.trainState(map, state.getStateID());
             break;
         case 1: //softmax�㷨
        	 str = Learning2.softmax.trainState(map, state.getStateID());
             break;
     }
		return str;
	}
	//����Fsche
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
	//����reward
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
	//���ֻ���ģ��
	private static Environment enviroSimulation(Parameter para) {
		Environment envi = new Environment();
		envi.Z = 0;
		int n; //�����Ľڵ���
		double[] p	= new double[100];
		envi.Z = calculateZ(envi.Z, para.p);
		
		for(double i = 0; i < StateParam.Nmax - StateParam.Nmin; i++) {
			p[(int) i] = Math.pow(Math.E, -1.5*Math.pow(3, 0.5)*Math.pow(para.a,2))*Math.pow(1.5*Math.pow(3, 0.5)*Math.pow(para.a,2)*para.lamdac, i)/ getNFactorial((long)i);
		}
		envi.N = selectN(p); //����N 
		
		return envi;
		
	}
	//ģ��ִ�д���
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
	//�����û�����
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
	//�����׳�
	public static long getNFactorial(long n){
        if(n==0){
            return 1;
        }
        return n*getNFactorial(n-1);
    }
	//ѡ��N�Ĵ�С
	private static int selectN(double[] p) {
		 double m = 0;
		 Random r = new Random(); //rΪ0��1�������
		 double t = r.nextDouble();
		 for (int i = 0; i < StateParam.Nmax - StateParam.Nmin; i++) {
		    /**
		     * �������������m~m+P[i]������Ϊѡ����i�����i��ѡ�еĸ�����P[i]
		     */
		        m = m + p[i];
		        if (t <= m) return i+StateParam.Nmin;
		    }
		return StateParam.Nmin;
	}
	//ѡ��Strategy
	private static Strategy SelectAction(Map<Long, Map<Strategy, Double>> map, State state, Task tsel) {
		//���ݸ�ʽ��������
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
	//��������ʱ��
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
	//����Fѡ��Strategy
	private static Strategy changeAction(State state, Task tsel) {
		Strategy str = Strategy.Local;
		List<Strategy> possibleAction =ActionFilter.getPossibleAction(state);
		Map<Strategy, Double> map = new HashMap<Strategy, Double>();
		double min = Double.POSITIVE_INFINITY;
		double delta=1.0/state.getN();//����ӵ���̶� 
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
	//���ɹ���ģ��
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
	//�������
	private static int getPossionVariable(double lamda) {  
        int x = 0;  
        double y = Math.random(), cdf = getPossionProbability(x, lamda);  
        while (cdf < y) {  
            x++;  
            cdf += getPossionProbability(x, lamda);  
        }  
        return x;  
    }  
	//����
    private static double getPossionProbability(int k, double lamda) {  
        double c = Math.exp(-lamda), sum = 1;  
        for (int i = 1; i <= k; i++) {  
            sum *= lamda / i;  
        }  
        return sum * c;  
    }  
	//��StrategyΪ����
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

