package simulation;

import reward.Strategy;

public class Parameter {
	
	
	public static  double fl = 0.1;
	public static  double fup = 0.15;
	public static  double fdown = 0.12;
	public static  double ft = 0.12;
	public static  double fad = 0.05;
	public static  double ps = 0.5;
	public static  double lamdan =0.25;
	public static  double lamdaq =0.25;
	public static  double lamdac =0.25;
	public static  double a = 1;
	public static  double p = 0.5;  //Z转移概率
	public static  Strategy str;
	
	public static double beita = 0.6;    //fsch判断阈值
	
	public static  int x = 0; //greedy算法选择 0在线 or 1离线
	public static final int n = 0;	//greedy算法选择 0reward or 1fsch
}
