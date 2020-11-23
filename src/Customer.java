/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author lct495
 */
public class Customer {
    private final int id;
    private final double betaPrice;
    private final double betaCS;
    private final double betaPT;
    private final double betaBike;
    private final double betaWalk;
    private final double betaWait;
    private int from;
    private int to;
    
    /**
     * Creates a customer given an id and a specification of its beta coefficients.
     * @param id
     * @param betaP
     * @param betaCS
     * @param betaPT
     * @param betaBI
     * @param betaWalk
     * @param betaWait 
     */
    public Customer(int id, double betaP, double betaCS, double betaPT, double betaBI, double betaWalk, double betaWait) {
        this.id = id;
        this.betaPrice = betaP;
        this.betaCS = betaCS;
        this.betaPT = betaPT;
        this.betaBike = betaBI;
        this.betaWalk = betaWalk;
        this.betaWait = betaWait;
    }

    public int getId() {
        return id;
    }

    public double getBetaPrice() {
        return betaPrice;
    }

    public double getBetaCS() {
        return betaCS;
    }

    public double getBetaPT() {
        return betaPT;
    }

    public double getBetaBike() {
        return betaBike;
    }

    public double getBetaWalk() {
        return betaWalk;
    }

    public double getBetaWait() {
        return betaWait;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }    

    public void setFrom(int from) {
        this.from = from;
    }

    public void setTo(int to) {
        this.to = to;
    }
        
    public void print(){
        System.out.println("Customer "+id+" from "+from+" to "+to+" Utility");
        System.out.println("BetaPrice "+betaPrice+" betaCS "+betaCS+" betaPT "+betaPT+" betaBike "+betaBike+" betaWalk "+betaWalk+" betaWait "+betaWait);
    }

    
    
}
