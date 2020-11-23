/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 *
 * @author lct495
 */
public class PricingProblem {
    private final Customer[] customers;
    private final List<Customer> customersDistributionFrom[];
    private final List<Customer> customersDistributionFromTo[][];
    
    private final int nZones;
    private final String[] zoneNames;
    private final int nVehicles;
    private final int nAlternatives;
    private final String[] alternativeName;
    private final double[] distance; // The distance of each zone from the center
    private final double[][] timeWithPublicTransport; // Between i and j   
    private final double[][] timeWithCarSharing; // Between i and j
    private final double[][] timeWithBicycle; // Between i and j
    private final double[][] timeWalkingCarSharing; // Between i and j when using carsharing
    private final double[][] timeWaitingCarSharing; // Between i and j when using service k
    private final double[][] timeWalkingPublicTransport; // Between i and j when using pt
    private final double[][] timeWaitingPublicTransport; // Between i and j when using pt
    private final double[][] timeWalkingBicycle; // Between i and j when using bicycle
    private final double[][] timeWaitingBicycle; // Between i and j when using bicycle
    private final double pricePerMinuteCS; // Price per minute CS
    private final double pricePT; // Price PT
    private final double priceBI; // Price bicycle
    private final double[] dropOffLevels; // F_ijl, the same for all i j 
    private final int[] initialZone; // X_vi
    private final double[][] relocationCost; // C^R_ij, same for all v 
    private final double[][] usageCost; // C^U_ij, same for all v
    private final double highestRelocationCost;
    private final int nScenarios;
    private final double alphaFrom;
    private final double alphaTo;
    private final double alphaVehicles;
    private final JDKRandomGenerator rng;

    

    public PricingProblem(int nCustomers, double betaPMin, double betaPMax,
            double referenceBetaCS, double referenceBetaPT, double referenceBetaBI, 
            double referenceBetaWalk, double referenceBetaWait, double customersVariability,
            int nZones, String[] zoneNames, double[] distance, double alphaFrom, double alphaTo, double alphaVehicles,
            int nVehicles,int nAlternatives, String[] alternativeName, 
            double timeCS[][], double timePT[][], double timeBI[][],
            double timeWalkCS[][], double timeWalkPT[][], double timeWalkBI[][],
            double timeWaitCS[][], double timeWaitPT[][], double timeWaitBI[][],
            double pricePerMinuteCS, double pricePT, double priceBI, double dropOffLevels[],
            double speed, double consumption, double fuelPrice, double perMinuteWage,int nScenarios,
            int seed, boolean identicalCustomers){       
        
        if(customersVariability > 1.0 || customersVariability < 0.0){
            throw new IllegalArgumentException("Customers variability must be in [0,1].");
        }
        this.alphaFrom = alphaFrom;
        this.alphaTo = alphaTo;
        this.alphaVehicles = alphaVehicles;
        this.rng = new JDKRandomGenerator();
        rng.setSeed(seed);
        // ===========================
        // Creates the customers
        // ===========================
        // First the array which will contain customers
        this.customers = new Customer[nCustomers];
        if(!identicalCustomers){
            // Then the distribution of beta^P, representing price sensitivity
            UniformRealDistribution udBetaPrice = new UniformRealDistribution(rng,betaPMin,betaPMax);   
            // Then, for the remaining betas, the distribution of variations from the respective reference betas
            UniformRealDistribution udVar = new UniformRealDistribution(rng,1.0-customersVariability,1.0+customersVariability);   

            for(int i = 1; i <= nCustomers; i++){
                // Samples a realization of each beta
                double betaP = udBetaPrice.sample();
                double betaCS = udVar.sample() * referenceBetaCS;
                double betaPT = udVar.sample() * referenceBetaPT;
                double betaBI = udVar.sample() * referenceBetaBI;
                double betaWalk = udVar.sample() * referenceBetaWalk;
                double betaWait = udVar.sample() * referenceBetaWait;
                // Finally creates the customer and stores it into the array
                this.customers[i-1] = new Customer(i,betaP,betaCS,betaPT,betaBI,betaWalk,betaWait);
            }
        }else{
            // Samples 1 or 0 indicating whether the customer is upper or lower middle class
            UniformRealDistribution zeroOrOne = new UniformRealDistribution(rng,0,1);   
            double betaP = Double.NEGATIVE_INFINITY;
            for(int i = 1; i <= nCustomers; i++){
                // Samples a realization of each beta
                if(zeroOrOne.sample() > 0.5){
                    betaP = betaPMin;
                }else{
                    betaP = betaPMax;
                }
                if(betaP == Double.NEGATIVE_INFINITY){
                    throw new IllegalArgumentException("Invalid betaP");
                }
                
                double betaCS = referenceBetaCS;
                double betaPT = referenceBetaPT;
                double betaBI = referenceBetaBI;
                double betaWalk = referenceBetaWalk;
                double betaWait = referenceBetaWait;
                // Finally creates the customer and stores it into the array
                this.customers[i-1] = new Customer(i,betaP,betaCS,betaPT,betaBI,betaWalk,betaWait);
            }
        }
        
        
        // =========================================
        // Saves the other configuration parameters
        // =========================================
        
        this.nZones = nZones;
        this.zoneNames = zoneNames;
        this.nVehicles = nVehicles;
        this.nAlternatives = nAlternatives;
        this.alternativeName = alternativeName;
        this.distance = distance;
        
        this.customersDistributionFrom = new List[nZones];
        this.customersDistributionFromTo = new List[nZones][nZones];
        // Calculates the average distance
        double averageDistance = 0;
        for(int i = 1; i <= nZones; i++){
            averageDistance += distance[i-1];
        }
        averageDistance = averageDistance / nZones;
        
        // ==================================
        // Distributes customers among zones
        // ==================================
        // Calculates the probabilities
        double gammaFrom[] = new double[nZones];
        double gammaTo[] = new double[nZones];
        double piFrom[] = new double[nZones];
        double piTo[] = new double[nZones];
        double sumFrom = 0;
        double sumTo = 0;
        for(int i = 1; i <= nZones; i++){
            double delta = distance[i-1] - averageDistance;
            gammaFrom[i-1] = Math.exp(- alphaFrom * delta);
            gammaTo[i-1] = Math.exp(- alphaTo * delta);
            sumFrom += gammaFrom[i-1] * distance[i-1];
            sumTo += gammaTo[i-1] * distance[i-1];
        }
        
        for(int i = 1; i <= nZones; i++){
            piFrom[i-1] = gammaFrom[i-1] * distance[i-1] / sumFrom;
            piTo[i-1] = gammaTo[i-1] * distance[i-1] / sumTo;
        }
        
        // Assigns customers to zones.
        // First it creates a distribution of customers to and from zones
        DiscreteDistribution dFrom = new DiscreteDistribution(piFrom, seed);
        DiscreteDistribution dTo = new DiscreteDistribution(piTo, seed+1);
        for(Customer c : customers){
            int from = dFrom.sample();
            int to = dTo.sample();
            int nAttempts = 0;
            while(to == from && nAttempts <= 10){
                to = dTo.sample();
                nAttempts++;
            }
            if(from == to){
                if(from != nZones){
                    from = from++;
                }else{
                    from = from--;
                }
            }
            
            c.setFrom(from);
            c.setTo(to);
            if(customersDistributionFrom[from-1] == null){
                customersDistributionFrom[from-1] = new ArrayList();
                customersDistributionFrom[from-1].add(c);
            }else{
                customersDistributionFrom[from-1].add(c);
            }
            if(customersDistributionFromTo[from-1][to-1] == null){
                customersDistributionFromTo[from-1][to-1] = new ArrayList();
                customersDistributionFromTo[from-1][to-1].add(c);
            }else{
                customersDistributionFromTo[from-1][to-1].add(c);
            }
        }
        
        // ==================================
        // Distributes vehicles among zones (initial position)
        // ==================================
        this.initialZone = new int[this.nVehicles];
        double gammaInitial[] = new double[nZones];
        double piInitial[] = new double[nZones];
        double sumInitial = 0;
        for(int i = 1; i <= nZones; i++){
            double delta = distance[i-1] - averageDistance;
            gammaInitial[i-1] = Math.exp(- alphaVehicles * delta);
            sumInitial += gammaInitial[i-1] * distance[i-1];
        }
        for(int i = 1; i <= nZones; i++){
            piInitial[i-1] = gammaInitial[i-1] * distance[i-1] / sumInitial;
        }
        DiscreteDistribution dInitial = new DiscreteDistribution(piInitial, seed+2);
        for(int v = 1; v <= nVehicles; v++){
            initialZone[v-1] = dInitial.sample();    
        }
        
        // ================================
        // Saves time parameters
        // ================================
        this.timeWithCarSharing = timeCS;
        this.timeWithPublicTransport = timePT;
        this.timeWithBicycle = timeBI;
        
        this.timeWalkingCarSharing = timeWalkCS;
        this.timeWalkingPublicTransport = timeWalkPT;
        this.timeWalkingBicycle = timeWalkBI;
        
        this.timeWaitingCarSharing = timeWaitCS;
        this.timeWaitingPublicTransport = timeWaitPT;
        this.timeWaitingBicycle = timeWaitBI;
        
        // ============================
        // Saves the price parameters
        // ============================
        this.pricePerMinuteCS = pricePerMinuteCS;
        this.pricePT = pricePT;
        this.priceBI = priceBI;
        this.dropOffLevels = dropOffLevels;
        
        // ===============================
        // Calculates the relocation and usage costs
        // ===============================
        this.relocationCost = new double[nZones][nZones];
        this.usageCost = new double[nZones][nZones];
        double highestRC = Double.NEGATIVE_INFINITY;
        for(int i = 1; i < nZones; i++){
            for(int j = i+1; j <= nZones; j++){
                relocationCost[i-1][j-1] = 
                        timeWithCarSharing[i-1][j-1] * (speed/60) * consumption * fuelPrice
                        +timeWithCarSharing[i-1][j-1] * perMinuteWage;
                if(relocationCost[i-1][j-1] > highestRC){
                    highestRC = relocationCost[i-1][j-1];
                }
                usageCost[i-1][j-1] = timeWithCarSharing[i-1][j-1] * (speed/60) * consumption * fuelPrice;
            }
        }
        this.highestRelocationCost = highestRC;
        this.nScenarios = nScenarios;
    }

    public Customer[] getCustomers() {
        return customers;
    }

    public int getnCustomers() {
        return customers.length;
    }
    
    public List<Customer> getCustomersDistribution(int i) {
        if(customersDistributionFrom[i-1] != null){
            return customersDistributionFrom[i-1];
        }else{
            return new ArrayList();
        }
    }
    
    public List<Customer> getCustomersDistribution(int i, int j) {
        if(customersDistributionFromTo[i-1][j-1] != null){
            return customersDistributionFromTo[i-1][j-1];
        }else{
            return new ArrayList();
        }
    }

    public int getnZones() {
        return nZones;
    }

    public String[] getZoneNames() {
        return zoneNames;
    }
    
    public int getnDropOffLevels(){
        return dropOffLevels.length;
    }

    public int getnVehicles() {
        return nVehicles;
    }

    public int getnAlternatives() {
        return nAlternatives;
    }

    public String[] getAlternativeName() {
        return alternativeName;
    }

    public double getPricePerMinuteCS() {
        return pricePerMinuteCS;
    }

    public double getPricePT() {
        return pricePT;
    }

    public double getPriceBI() {
        return priceBI;
    }

    public double[] getDropOffLevels() {
        return dropOffLevels;
    }
    
    public int getnServices(){
        return nVehicles + nAlternatives;
    }

    public int getnScenarios() {
        return nScenarios;
    }

    public double getAlphaFrom() {
        return alphaFrom;
    }

    public double getAlphaTo() {
        return alphaTo;
    }

    public double getAlphaVehicles() {
        return alphaVehicles;
    }
    
    public Service getService(int v){
        if(v <= nVehicles){
            return Service.CARSHARING;
        }else if(v == nVehicles + 1){
            return Service.PUBLICTRANSPORT;
        }else if(v == nVehicles +2){
            return Service.BICYCLE;
        }else{
            throw new IllegalArgumentException("Invalid service "+v);
        }
    }
    
    /**
     * Returns true if the service is an alternative service.
     * @param v
     * @return 
     */
    public boolean isAlternative(int v){
        return v > nVehicles;
    }
    
    public double getRelocationCost(int i, int j){
        if(i > j){
            return getRelocationCost(j,i);
        }else{
            return relocationCost[i-1][j-1];
        }
    }

    public double getHighestRelocationCost() {
        return highestRelocationCost;
    }
    
    public double getUsageCost(int i, int j){
        if(i > j){
            return getUsageCost(j,i);
        }else{
            return usageCost[i-1][j-1];
        }
    }
    
    public double getTravelTimeCarSharing(int i, int j){
        if(i > j){
            return getTravelTimeCarSharing(j,i);
        }else{
            return timeWithCarSharing[i-1][j-1];
        }
    }
    public double getTravelTimePublicTransport(int i, int j){
        if(i > j){
            return getTravelTimePublicTransport(j,i);
        }else{
            return timeWithPublicTransport[i-1][j-1];
        }
    }
    public double getTravelTimeBicycle(int i, int j){
        if(i > j){
            return getTravelTimeBicycle(j,i);
        }else{
            return timeWithBicycle[i-1][j-1];
        }
    }
    public double getWalkingTimeWithCarSharing(int i, int j){
        if(i > j){
            return getWalkingTimeWithCarSharing(j,i);
        }else{
            return timeWalkingCarSharing[i-1][j-1];
        }
    }
    public double getWalkingTimeWithPublicTransport(int i, int j){
        if(i > j){
            return getWalkingTimeWithPublicTransport(j,i);
        }else{
            return timeWalkingPublicTransport[i-1][j-1];
        }
    }
    public double getWalkingTimeWithBicycle(int i, int j){
        if(i > j){
            return getWalkingTimeWithBicycle(j,i);
        }else{
            return timeWalkingBicycle[i-1][j-1];
        }
    }
    public double getWaitingTimeWithCarSharing(int i, int j){
        if(i > j){
            return getWaitingTimeWithCarSharing(j,i);
        }else{
            return timeWaitingCarSharing[i-1][j-1];
        }
    }
    public double getWaitingTimeWithPublicTransport(int i, int j){
        if(i > j){
            return getWaitingTimeWithPublicTransport(j,i);
        }else{
            return timeWaitingPublicTransport[i-1][j-1];
        }
    }
    public double getWaitingTimeWithBicycle(int i, int j){
        if(i > j){
            return getWaitingTimeWithBicycle(j,i);
        }else{
            return timeWaitingBicycle[i-1][j-1];
        }
    }
    
    public int getInitialZone(int v){
        return initialZone[v-1];
    }
    
    public int isInZone(int v, int i){
        if(initialZone[v-1] == i){
            return 1;
        }else{
            return 0;
        }
    }
    /**
     * Returns the utility for a given i-j, service and drop-off-fee.
     * @param i
     * @param j
     * @param k
     * @param v
     * @param dropOffFee
     * @return 
     */
    public double getUtility(int i, int j, Customer k, int v, double dropOffFee){
        Service s = getService(v);
        double utility = 0;
        switch(s){
            case CARSHARING: utility = utility + k.getBetaPrice() * (getPricePerMinuteCS() * getTravelTimeCarSharing(i, j) + dropOffFee)
                    + k.getBetaCS() * getTravelTimeCarSharing(i, j) 
                    + tauFunction(getWalkingTimeWithCarSharing(i, j)) * k.getBetaWalk() * getWalkingTimeWithCarSharing(i, j)
                    + k.getBetaWait() * getWaitingTimeWithCarSharing(i, j);
            break;
            case PUBLICTRANSPORT: utility = utility 
                    + k.getBetaPrice() * getPricePT()
                    + k.getBetaPT() * getTravelTimePublicTransport(i, j)
                    + tauFunction(getWalkingTimeWithPublicTransport(i, j)) * k.getBetaWalk() * getWalkingTimeWithPublicTransport(i, j)
                    + k.getBetaWait() * getWaitingTimeWithPublicTransport(i, j);
            break;
            case BICYCLE: utility = utility 
                    + k.getBetaPrice() * getPriceBI()
                    + tauFunction(getTravelTimeBicycle(i, j)) * k.getBetaBike() * getTravelTimeBicycle(i, j)
                    + tauFunction(getWalkingTimeWithBicycle(i, j)) * k.getBetaWalk() * getWalkingTimeWithBicycle(i, j)
                    + k.getBetaWait() * getWaitingTimeWithBicycle(i, j);
            break;
            default: throw new IllegalArgumentException("Invalid transportation means");
        }
        return utility;
    }
    /**
     * Returns the utility for a given i-j and alternative service.
     * @param i
     * @param j
     * @param k
     * @param v
     * @return 
     */
    public double getUtilityAlternative(int i, int j, Customer k, int v){
        Service s = getService(v);
        if(s.equals(Service.CARSHARING)){
            throw new IllegalArgumentException("This method cannot be applied to car-sharing services");
        }
        double utility = 0;
        switch(s){
            case PUBLICTRANSPORT: utility = utility 
                    + k.getBetaPrice() * getPricePT()
                    + k.getBetaPT() * getTravelTimePublicTransport(i, j)
                    + tauFunction(getWalkingTimeWithPublicTransport(i, j)) * k.getBetaWalk() * getWalkingTimeWithPublicTransport(i, j)
                    + k.getBetaWait() * getWaitingTimeWithPublicTransport(i, j);
            break;
            case BICYCLE: utility = utility 
                    + k.getBetaPrice() * getPriceBI()
                    + tauFunction(getTravelTimeBicycle(i, j)) * k.getBetaBike() * getTravelTimeBicycle(i, j)
                    + tauFunction(getWalkingTimeWithBicycle(i, j)) * k.getBetaWalk() * getWalkingTimeWithBicycle(i, j)
                    + k.getBetaWait() * getWaitingTimeWithBicycle(i, j);
            break;
            default: throw new IllegalArgumentException("Invalid transportation means");
        }
        return utility;
    }
    /**
     * Returns the portion of the utility that does not depend on the price.
     * @param i
     * @param j
     * @param k
     * @param v
     * @return 
     */
    public double getConstantUtility(int i, int j, Customer k, int v){
        Service s = getService(v);
        double utility = 0;
        switch(s){
            case CARSHARING: utility = utility 
                    + k.getBetaCS() * getTravelTimeCarSharing(i, j) 
                    + tauFunction(getWalkingTimeWithCarSharing(i, j)) * k.getBetaWalk() * getWalkingTimeWithCarSharing(i, j)
                    + k.getBetaWait() * getWaitingTimeWithCarSharing(i, j);
            break;
            case PUBLICTRANSPORT: utility = utility 
                    + k.getBetaPT() * getTravelTimePublicTransport(i, j)
                    + tauFunction(getWalkingTimeWithPublicTransport(i, j)) * k.getBetaWalk() * getWalkingTimeWithPublicTransport(i, j)
                    + k.getBetaWait() * getWaitingTimeWithPublicTransport(i, j);
            break;
            case BICYCLE: utility = utility 
                    + tauFunction(getTravelTimeBicycle(i, j)) * k.getBetaBike() * getTravelTimeBicycle(i, j)
                    + tauFunction(getWalkingTimeWithBicycle(i, j)) * k.getBetaWalk() * getWalkingTimeWithBicycle(i, j)
                    + k.getBetaWait() * getWaitingTimeWithBicycle(i, j);
            break;
            default: throw new IllegalArgumentException("Invalid transportation means");
        }
        return utility;
    }
    
    
    /**
     * Generates the requests of the customers wishing to use carsharing in a given scenario.
     * @param scenario
     * @return 
     */
    public List<Request> generateRequests(double scenario[][]){
        // 1. For each customer determines l* as the highest drop off fee 
        // at which they are willing to use car sharing, prefering it to alternatives.
        // Each customer for which such l* exists corresponds to a request.
        List<Request> requests = new ArrayList();
        
        for(Customer k : getCustomers()){
            int i = k.getFrom();
            int j = k.getTo();
            //System.out.println("Customer "+k.getId()+" from "+i+" to "+j); 
            double utilityPT = getUtilityAlternative(i, j, k, getnVehicles()+1) + scenario[k.getId()-1][getnVehicles()];
            double utilityBI = getUtilityAlternative(i, j, k, getnVehicles()+2) + scenario[k.getId()-1][getnVehicles()+1];
            double maxAlternative = Math.max(utilityPT, utilityBI);
            //System.out.println("Max alternative = "+maxAlternative);
            int highestLevel = Integer.MIN_VALUE;
            // Finds the highest drop off fee at which the customer 
            // uses carsharing
            for(int l = 1; l <= getnDropOffLevels(); l++){
                double utilityCS = getUtility(i, j, k, 1, getDropOffLevels()[l-1]) + scenario[k.getId()-1][0];
                //System.out.println("Utility cs level "+l+" "+utilityCS);
                if(utilityCS >= maxAlternative){
                    highestLevel = l;
                }else{
                    break;
                }
            }
            // If such drop off fee exists it creates a request
            if(highestLevel > Integer.MIN_VALUE){
                requests.add(new Request(k,i,j,highestLevel));
            }
        }            
        return requests;
    }
    
    /**
     * Generates the list of requests of the customers wishing to use carsharing for each scenario.
     * @param scenarios
     * @return 
     */
    public List<Request>[] generateRequests(List<double[][]> scenarios){
        // 0. For each scenario finds the customer requests
        System.out.println("GENERATING REQUESTS");
        List<Request> requests[] = new List[scenarios.size()];
        for(int s = 1; s <= nScenarios; s++){
            System.out.println("SCENARIO "+s);
            // 1. For each customer determines l* as the highest drop off fee 
            // at which they are willing to use car sharing, prefering it to alternatives.
            // Each customer for which such l* exists corresponds to a request.
            requests[s-1] = new ArrayList();
            
            for(Customer k : getCustomers()){
                int i = k.getFrom();
                int j = k.getTo();
                //System.out.println("Customer "+k.getId()+" from "+i+" to "+j); 
                double utilityPT = getUtilityAlternative(i, j, k, getnVehicles()+1) + scenarios.get(s-1)[k.getId()-1][getnVehicles()];
                double utilityBI = getUtilityAlternative(i, j, k, getnVehicles()+2) + scenarios.get(s-1)[k.getId()-1][getnVehicles()+1];
                double maxAlternative = Math.max(utilityPT, utilityBI);
                //System.out.println("Max alternative = "+maxAlternative);
                int highestLevel = Integer.MIN_VALUE;
                // Finds the highest drop off fee at which the customer 
                // uses carsharing
                for(int l = 1; l <= getnDropOffLevels(); l++){
                    double utilityCS = getUtility(i, j, k, 1, getDropOffLevels()[l-1]) + scenarios.get(s-1)[k.getId()-1][0];
                    //System.out.println("Utility cs level "+l+" "+utilityCS);
                    if(utilityCS >= maxAlternative){
                        highestLevel = l;
                    }else{
                        break;
                    }
                }
                // If such drop off fee exists it creates a request
                if(highestLevel > Integer.MIN_VALUE){
                    requests[s-1].add(new Request(k,i,j,highestLevel));
                }
            }
                    
        }
        return requests;
    }
    
    
    public double getExpectedRevenueUpperBound(List<Request>[] scenarioRequests){
        double expectedRevenue = 0;
        for(int s = 1; s <= nScenarios; s++){
            double scenarioRevenue = 0;
            List<Request> requests = scenarioRequests[s-1];
            for(Request r : requests){
                scenarioRevenue = scenarioRevenue + Math.max(0, r.getRevenue(this));
            }
            expectedRevenue = expectedRevenue + (1.0/nScenarios) * scenarioRevenue;  
        }
        return expectedRevenue;
    }
    
    public double getExpectedRevenueUpperBound(List<Request> requests){
        double revenue = 0;
        for(Request r : requests){
            revenue = revenue + Math.max(0, r.getRevenue(this));
        }
        return revenue;
    }
    
    
    public void print(){
        System.out.println("*******************************************");
        System.out.println("INITIAL DISTRIBUTION OF CARS");
        for(int v = 1; v <= nVehicles; v++){
            System.out.println("Car "+v+" in zone "+initialZone[v-1]+"("+zoneNames[initialZone[v-1]-1]+" - distance from the center "+distance[initialZone[v-1]-1]+")");    
        }
        System.out.println("INITIAL DISTRIBUTION OF CUSTOMERS");
        for(int i = 1; i <= nZones; i++){
            for(int j = 1; j <= nZones; j++){
                if(customersDistributionFromTo[i-1][j-1] != null){
                    System.out.println("From zone "+i+"("+zoneNames[i-1]+"-"+distance[i-1]+") to zone "+j+"("+zoneNames[j-1]+"-"+distance[i-1]+")");
                    for(Customer c:customersDistributionFromTo[i-1][j-1]){
                        //System.out.println("Customer "+c.getId());
                        c.print();
                    }
                }
            }
        }
        System.out.println("*******************************************");
        
        
    }
    
    // Utils
    private double tauFunction(double time){
        return Math.ceil(time/10);
    }
}
