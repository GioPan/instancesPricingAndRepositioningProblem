/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import org.apache.commons.math3.random.JDKRandomGenerator;

/**
 *
 * @author lct495
 */
public class DiscreteDistribution {
    
    private final double pmf[];
    private final double cdf[];
    private final JDKRandomGenerator rng;

    public DiscreteDistribution(double[] pmf, int seed) {
        this.pmf = pmf;
        this.rng = new JDKRandomGenerator();
        rng.setSeed(seed);
        
        double totalProbability = 0;
        this.cdf = new double[pmf.length];
        for(int i = 1; i <= pmf.length; i++){
            if(i == 1){
                cdf[i-1] = pmf[i-1];
                totalProbability += pmf[i-1];
            }else{
                cdf[i-1] = cdf[i-2] + pmf[i-1];
                totalProbability += pmf[i-1];
            }
        }
        if(totalProbability >= 1.001 || totalProbability <= 0.999){
            throw new IllegalArgumentException("Invalid PMF.");
        }
    }
    
    public void print(){
        for(int i = 1; i <= pmf.length; i++){
            System.out.print(String.format("%5.2f", pmf[i-1]));
        }
        System.out.println("");
        for(int i = 1; i <= pmf.length; i++){
            System.out.print(String.format("%5.2f", cdf[i-1]));
        }
        System.out.println("");
    }
    
    public int sample(){
        double p = rng.nextDouble();
        int sample = -1;
        for(int i = 1; i <= cdf.length; i++){
            double lb = 0;
            if(i > 1){
                lb = cdf[i-2];
            }
            double ub = cdf[i-1];
            if(p > lb && p <= ub){
                sample = i;
                break;
            }
        }
        if(sample == -1){
            throw new RuntimeException("Could not sample.");
        }
        return sample;
    }
    
    
    
}
