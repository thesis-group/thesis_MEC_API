package simulation;

import reward.Strategy;

public class Parameter {
	
	
	public static  double fl = 0.5;
	public static  double fup = 0.5;
	public static  double fdown = 0.5;
	public static  double ft = 0.5;
	public static  double fad = 0.5;
	public static  double ps = 0.5;
	public static  double lamdan =1;
	public static  double lamdaq =1;
	public static  double lamdac =1;
	public static  double a = 1;
	public static  double p = 0.5;
	public static  Strategy str;
	public static  int x = 0; //greedy算法选择 0在线 or 1离线
	public static final int n = 0;	//greedy算法选择 0reward or 1fsch
}
