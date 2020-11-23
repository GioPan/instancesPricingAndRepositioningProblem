/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author lct495
 */
public class Request {
    private final Customer k;
    private final int from;
    private final int to;
    private final int dropOffLevel;

    public Request(Customer k, int from, int to, int dropOffLevel) {
        this.k = k;
        this.from = from;
        this.to = to;
        this.dropOffLevel = dropOffLevel;
    }

    public Customer getK() {
        return k;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getDropOffLevel() {
        return dropOffLevel;
    }
    
    /**
     * Returns the revenue obtainable if the request is accepted.
     * @param pp 
     * @return 
     */
    public double getRevenue(PricingProblem pp){
        double tripRevenue = pp.getDropOffLevels()[dropOffLevel-1] + pp.getPricePerMinuteCS() * pp.getTravelTimeCarSharing(from, to) - pp.getUsageCost(from, to);
        return tripRevenue;
    }
    /**
     * Returns the revenue obtainable if the request is accepted.
     * @param pp 
     * @param dropOffLevel 
     * @return 
     */
    public double getRevenue(PricingProblem pp,int dropOffLevel){
        double tripRevenue = pp.getDropOffLevels()[dropOffLevel-1] + pp.getPricePerMinuteCS() * pp.getTravelTimeCarSharing(from, to) - pp.getUsageCost(from, to);
        if(dropOffLevel <= this.dropOffLevel){    
            return tripRevenue;
        }else{
            return -tripRevenue;
        }
    }


    
}
