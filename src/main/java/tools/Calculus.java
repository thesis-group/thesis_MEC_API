package tools;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;

import java.util.function.Function;

public class Calculus {
    /**
     * 一元定积分计算工具
     * @param function  一元方程的lambda表达式 形如 x->2*x
     * @param lower     积分下限
     * @param upper     积分上限
     * @return   积分值{double}
     */
    public static double Integrate(Function<Double,Double> function, double lower, double upper){
        UnivariateFunction f = new UnivariateFunction() {
            @Override
            public double value(double x) {
                return function.apply(x);
            }
        };
        SimpsonIntegrator integrator = new SimpsonIntegrator();
        return  integrator.integrate(100,f,lower,upper);
    }
}
