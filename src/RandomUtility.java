/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import org.apache.commons.math3.distribution.GumbelDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 *
 * @author lct495
 */
public class RandomUtility {
    
    private final GumbelDistribution gd;
    /**
     * Creates the random utility term.
     * @param pp 
     * @param seed 
     */
    public RandomUtility(PricingProblem pp, int seed) {
        // Calculates the standard deviation of the deterministic part of the 
        // utility function
        SummaryStatistics stats = new SummaryStatistics();
        for(int i = 1; i <= pp.getnZones(); i++){
            for(int j = 1; j <= pp.getnZones(); j++){
                for(Customer c : pp.getCustomers()){
                    for(int v = 1; v <= pp.getnServices(); v++){
                        if(pp.isAlternative(v)){
                            stats.addValue(pp.getUtility(i, j,c, v, 0));
                        }else{
                            for(double l : pp.getDropOffLevels()){
                                stats.addValue(pp.getUtility(i, j,c, v, l));
                            }
                        }
                    }
                }
            }
        }
        double utilitySTD = stats.getStandardDeviation();
        
        // Creates a suitable Gumbel distribution (Extreme Value Distribution type I)
        JDKRandomGenerator rng = new JDKRandomGenerator();
        rng.setSeed(seed);
        double mean = 0;
        double beta = utilitySTD * Math.sqrt(6)/Math.PI;
        double mu = mean - Gamma.GAMMA * beta;
        
        gd = new GumbelDistribution(rng,mu,beta);
        
        
    }
    /**
     * Generates a sample of the random utility term.
     * @return 
     */
    public double sample(){
        return gd.sample();
    }
    
    
    
}
