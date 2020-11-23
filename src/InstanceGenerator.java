/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


/**
 *
 * @author lct495
 */
public class InstanceGenerator {
    
    public static PricingProblem generate(String instancesFolder, int configNo, String city, int seed, int nVehicles, int nCustomers, int nScenarios, boolean identicalCustomers) throws FileNotFoundException{
        
        // =====================================================
        // Reads the instance data
        // =====================================================
        
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(instancesFolder+File.separator+"config_"+configNo+".txt")));
        
        int nAlternatives = scanner.nextInt();
        scanner.nextLine();
        
        String alternativeNames[] = new String[nAlternatives];
        for(int a = 1; a <= nAlternatives; a++){
            alternativeNames[a-1] = scanner.next();
        }
        scanner.nextLine();
        
        double betaPriceLow = scanner.nextDouble();
        double betaPriceHigh = scanner.nextDouble();
        scanner.nextLine();
        
        double referenceBetaCS = scanner.nextDouble();
        double referenceBetaPT = scanner.nextDouble();
        double referenceBetaBI = scanner.nextDouble();
        double referenceBetaWalk = scanner.nextDouble();
        double referenceBetaWait = scanner.nextDouble();
        scanner.nextLine();
        
        double customersVariability = scanner.nextDouble();
        scanner.nextLine();
        
        double priceCS = scanner.nextDouble();
        scanner.nextLine();
        
        double pricePT = scanner.nextDouble();
        scanner.nextLine();
        
        double priceBI = scanner.nextDouble();
        scanner.nextLine();
        
        int minDropOffFee = scanner.nextInt();
        scanner.nextLine();
        
        int maxDropOffFee = scanner.nextInt();
        scanner.nextLine();
        
        double dropOffFees[] = new double[maxDropOffFee - minDropOffFee + 1];
        double currentDropOffFee = minDropOffFee;
        for(int l = 1; l <= dropOffFees.length; l++){
            dropOffFees[l-1] = currentDropOffFee;
            currentDropOffFee++;
        }
        
        int nZones = scanner.nextInt();
        scanner.nextLine();
        
        double alphaFrom = scanner.nextDouble();
        double alphaTo = scanner.nextDouble();
        double alphaInitial = scanner.nextDouble();
        scanner.nextLine();
        
        double speed = scanner.nextDouble();
        scanner.nextLine();
        double consumption = scanner.nextDouble();
        scanner.nextLine();
        double fuelPrice = scanner.nextDouble();
        scanner.nextLine();
        double perMinuteWage = scanner.nextDouble();
        scanner.nextLine();
        
        
        // =====================================================
        // Reads the zones
        // =====================================================
        String zoneNames[] = new String[nZones];
        double distance[] = new double[nZones];
        scanner = new Scanner(new BufferedReader(new FileReader(instancesFolder+File.separator+"zones_"+city+".txt")));
        // Skips the header
        scanner.nextLine();
        for(int i = 1; i <= nZones; i++){
            int id = scanner.nextInt();
            if (id != i){
                throw new IllegalArgumentException("Invalid instance file.");
            }
            zoneNames[i-1] = scanner.next();
            distance[i-1] = scanner.nextDouble();
        }
        
        // =====================================================
        // Reads the travel times
        // =====================================================
        double timePT[][] = new double[nZones][nZones];
        double timeCS[][] = new double[nZones][nZones];
        double timeBI[][] = new double[nZones][nZones];
        double timeWalkCS[][] = new double[nZones][nZones];
        double timeWaitCS[][] = new double[nZones][nZones];
        double timeWalkPT[][] = new double[nZones][nZones];
        double timeWaitPT[][] = new double[nZones][nZones];
        double timeWalkBike[][] = new double[nZones][nZones];
        double timeWaitBike[][] = new double[nZones][nZones];
        
        scanner = new Scanner(new BufferedReader(new FileReader(instancesFolder+File.separator+"times_"+city+".txt")));
        // Skips the header
        scanner.nextLine();
        for(int i = 1; i <= nZones-1; i++){
            for(int j = i+1; j <= nZones; j++){
                for(int s = 1; s <= 3; s++){
                    int from = scanner.nextInt();
                    int to = scanner.nextInt();
                    String service = scanner.next();
                    double tcs = scanner.nextDouble();
                    double tpt = scanner.nextDouble();
                    double twalk = scanner.nextDouble();
                    double tbike = scanner.nextDouble();
                    double twait = scanner.nextDouble();
                    switch(service){
                            case "CS": 
                                timeCS[i-1][j-1] = tcs;
                                timeWalkCS[i-1][j-1] = twalk;
                                timeWaitCS[i-1][j-1] = twait;
                                break;
                            case "PT":
                                timePT[i-1][j-1] = tpt;
                                timeWalkPT[i-1][j-1] = twalk;
                                timeWaitPT[i-1][j-1] = twait;
                                break;
                            case "B":
                                timeBI[i-1][j-1] = tbike;
                                timeWalkBike[i-1][j-1] = twalk;
                                timeWaitBike[i-1][j-1] = twait;
                                break;
                            default : throw new IllegalArgumentException("Invalid instance file");
                                    }
                }
            }
        }
         
        return new PricingProblem(nCustomers, betaPriceLow,  betaPriceHigh,
                referenceBetaCS,  referenceBetaPT,  referenceBetaBI,
                referenceBetaWalk,  referenceBetaWait,  customersVariability,
                nZones, zoneNames,distance,alphaFrom,alphaTo,alphaInitial,
                nVehicles,nAlternatives,alternativeNames, 
                timeCS, timePT, timeBI,
                timeWalkCS, timeWalkPT, timeWalkBike,
                timeWaitCS, timeWaitPT, timeWaitBike,
                priceCS, pricePT, priceBI,dropOffFees,
                speed, consumption, fuelPrice, perMinuteWage,
                nScenarios,
                seed, identicalCustomers);
        
        
    }
    public static RandomUtility generateRandomUtility(PricingProblem pp, int seed){
        return new RandomUtility(pp,seed);
    }
    
}
