package simulation;

import java.util.List;
import java.util.Map;
import java.util.Random;

import model.*;
import param.*;

public class Simulation {
	public void DiaoDu(State state, double fsc, double beita, List<Task> task) {
		List<Task> q = null; //�������
		List<Task> e = null; //��ת����
		int el = 0;//��ת���г���
		Task tsel = null; //��ʱ�������
		int a = 0;
		
		//�������ָ���ֲ���ʱ��������Tn 
		double lamda = 2, t = 0;
		int Tmax = task.size();
		int i = 1, rw = 1;//��ǰ���������ź͵�������ı��
		double [] t1 = new double[Tmax];
		double [] t2 = new double [Tmax];
		t1[0] = 0;
		//����t1Ϊÿ������ĵ���ʱ����±�1��ʼ
		while(i <= Tmax) {
			t1[i] = RandExp(lamda) + t1[i - 1];
			i++;
		}
		i = 1;
		q.add(task.get(0));	//����һ������ֱ�ӿ�ʼ����Q���о���
		
		while(!q.isEmpty()) {
			rw++;
			//����e����ת
			while(t1[rw] < t) {
				e.add(task.get(rw));
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
			//��ʼ����
			tsel = q.get(0);
			a = SelectAction(state,tsel);
			t2[i] = runtime(i,a, state, tsel);
			//ɾ������������
			q.remove(tsel);
			i++;
			//�´ξ���ʱ��
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
		double delta=1.0/state.getN();//����ӵ���̶� 
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
        	//���ڲ���������ܶȣ���֤�Ȳ�����С����Ϊʱ��Ϊ��
            p = rand.nextDouble();
            if (p < lamda)
                break;
        }
        randres = -temp * Math.log(temp * p);
        return randres;
    }
	
	
}

