package simulation;

import java.util.List;
import java.util.Map;
import java.util.Random;

import model.*;
import param.*;
import reward.*;

public class Simulation {
	public void DiaoDu(Map<Long, Map<Strategy,Double>> map, double fsc, double beita, List<Task> task, int n) {
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
			t1[i] = RandExp(lamda) + t1[i - 1];
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
			
			//����state
			
			
			//��ʼ����
			tsel = q.get(0); //7
			str = SelectAction(map, state, tsel);	//8
			
			fsch = carculateFsche(state, str, tsel); //9
			rsa = Reward.getReward(state, str, tsel).getCost(); //11
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
		return;
	}

	

	
	
	
	
	private double carculateFsche(State state, Strategy str, Task tsel) {
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

	private int getStrategy(Strategy str) {
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

	private RewardBackValue rewardR(int n) {
		switch(n) {
		case 0://����
			
			break;
		case 1://����
			
			break;
		}
		return null;
	}

	private Environment enviroSimulation(Parameter para) {
		Environment envi = null;
		int n; //�����Ľڵ���
			
		
		
		
		
		n = 0;
		double delta=1.0/n;//����ӵ���̶� 
				
		
		
		
		return envi;
		
	}

	private Strategy SelectAction(Map<Long, Map<Strategy, Double>> map, State state, Task tsel) {
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

	private Strategy changeAction(State state, Task tsel) {
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
        	//���ڲ���������ܶȣ���֤�Ȳ�����С����Ϊʱ��Ϊ��
            p = rand.nextDouble();
            if (p < lamda)
                break;
        }
        randres = -temp * Math.log(temp * p);
        return randres;
    }
	
	
}

