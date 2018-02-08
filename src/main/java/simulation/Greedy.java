package simulation;

import java.util.List;
import java.util.Map;
import java.util.Random;

import model.State;
import model.Task;
import param.AdHocParam;
import param.CloudletParam;
import param.LocalParam;
import reward.Reward;
import reward.RewardBackValue;
import reward.Strategy;
import reward.offline.RewardOFL;
import reward.offline.StrategyOFL;

public class Greedy {
	public void greedy(Map<Long, Map<Strategy,Double>> map, double fsc, double beita, List<Task> task, int n) {
		Strategy str = null;
		State state = null;
		Task tsel = null;
		Parameter para = null;
		
		int Tmax = task.size();
		double [] t1 = new double[Tmax+1];
		double [] t2 = new double [Tmax+1];
		int[] tw = new int[Tmax+1];
		double lamda = 2, t = 0;
		int i = 1;
		
		t1[0] = 0;
		//存入t1为每个任务的到达时间从下标1开始
		while(i <= Tmax) {
			t1[i] = RandExp(lamda) + t1[i - 1];
			i++;
		}
		i = 1;
		
		
		//环境模拟
		Environment envi = enviroSimulation(para);
		
		//更新state
		state = new State(envi.Z, 50, envi.N, tw[i]);
		
		
		
		switch(n) {
		case 0://reward
			
		case 1: //fsch
			str = changeAction(state, tsel);
		}
			
	}
	
	private Environment enviroSimulation(Parameter para) {
		// TODO 自动生成的方法存根
		return null;
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
	
	private  RewardBackValue rewardR(State state, Strategy str, Task tsel, int n) {
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
	
	private Strategy changeAction(State state, Task tsel) {
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

	
	
	
}
