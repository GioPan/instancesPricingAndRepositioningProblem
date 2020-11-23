/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;

/**
 *
 * @author lct495
 */
public class OptionValues {
    
    private final int config;
    private final int nVehicles;
    private final int nCustomers;
    private final int instanceNumber;
    private final int nScenarios;
    private final boolean identicalCustomers;
    private final String instanceDirectory;
    private final String city;
    private final boolean verbose;
    
    
    /**
     * Creates an OptionValues instance.
     * @param config
     * @param city
     * @param nVehicles
     * @param nCustomers
     * @param instanceNumber
     * @param identicalCustomers
     * @param instanceDirectory 
     * @param verbose 
     * @param nScenarios 
     */
    public OptionValues(int config, String city, int nVehicles, int nCustomers, 
            int instanceNumber, boolean identicalCustomers, String instanceDirectory,
            boolean verbose,
            int nScenarios) {
        this.config = config;
        this.nVehicles = nVehicles;
        this.nCustomers = nCustomers;
        this.instanceNumber = instanceNumber;
        this.identicalCustomers = identicalCustomers;
        this.instanceDirectory = instanceDirectory;
        this.city = city;
        this.verbose = verbose;
        this.nScenarios = nScenarios;
    }

    public int getnVehicles() {
        return nVehicles;
    }

    public int getnCustomers() {
        return nCustomers;
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }
    
    public boolean identicalCustomers(){
        return identicalCustomers;
    }
    
    public String getInstanceDirectory() {
        return instanceDirectory;
    }

    public int getConfig() {
        return config;
    }

    public String getCity() {
        return city;
    }
   
    public boolean verbose(){
        return verbose;
    }

    public int getnScenarios() {
        return nScenarios;
    }
}
