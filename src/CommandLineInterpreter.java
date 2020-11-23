/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author lct495
 */
public class CommandLineInterpreter {
    private final Options cliOptions;
    
    /**
     * Builds the CLI.
     * Defines the available options to read.
     */
    public CommandLineInterpreter() {
        
        // Defines the available options
        cliOptions = new Options(); 
        
        // Creates the header and footer to print in the help message.
        String header = "Pricing-based repositioning problem.";
        String footer = "Please report any issue at gp@math.ku.dk.";
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Pricing", header, cliOptions, footer, true);
        
        // Creates the options
        Option v_opt = new Option("v", true, "The number of vehicles.");        
        v_opt.setLongOpt("nVehicles");
        v_opt.setRequired(true);
        cliOptions.addOption(v_opt);
        
        Option c_opt = new Option("c", true, "The number of customers.");        
        c_opt.setLongOpt("nCustomers");
        c_opt.setRequired(true);
        cliOptions.addOption(c_opt);
        
        Option nScenarios_opt = new Option("s", true, "The number of scenarios.");        
        nScenarios_opt.setLongOpt("scenarios");
        cliOptions.addOption(nScenarios_opt);
        
       
        Option instanceDirectory_opt = new Option("idir", true, "The folder of containing instance files.");        
        instanceDirectory_opt.setLongOpt("instanceDir");
        cliOptions.addOption(instanceDirectory_opt);
        
        
        Option i_opt = new Option("i", true, "The number of the instance.");        
        i_opt.setLongOpt("instanceNumber");
        cliOptions.addOption(i_opt);
        
        Option city_opt = new Option("city", true, "The city.");        
        cliOptions.addOption(city_opt);
        
        Option config_opt = new Option("conf", true, "The configuration number.");        
        config_opt.setLongOpt("configurationNumber");
        cliOptions.addOption(config_opt);
        
        Option verbose_opt = new Option("verb", false, "Prints information about the instance.");        
        verbose_opt.setLongOpt("verbose");
        cliOptions.addOption(verbose_opt);
        
        
        Option identicalCustomers_opt = new Option("ic", false, "Chooses whether the customers should be identical. "
                + "Identical customers have the same utility function and are different only in the random term. Non-identical customers are characterized by individual utility functions.");        
        identicalCustomers_opt.setLongOpt("identicalCustomers");
        cliOptions.addOption(identicalCustomers_opt);
        
    }
    
    /**
     * Parses command line instructions and returns their value.
     * @param args
     * @return 
     * @throws org.apache.commons.cli.ParseException 
     */
    public OptionValues parse(String[] args) throws ParseException{
        // Creates a parser
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(cliOptions, args);
        
        
        // Reads the configuration number
        int config = 0;
        if(cmd.hasOption("conf")){
            String value = cmd.getOptionValue("conf");
            if(value == null){
                throw new IllegalArgumentException("Please specify the configuration number.");
            }else{
                config = Integer.parseInt(value);
                if(config < 0){
                    throw new IllegalArgumentException("The configuration number must non-negative.");
                }
            }
        }
        
        // Reads the instance number
        int instance = 1;
        if(cmd.hasOption("i")){
            String value = cmd.getOptionValue("i");
            if(value == null){
                throw new IllegalArgumentException("Please specify the instance number.");
            }else{
                instance = Integer.parseInt(value);
                if(instance < 1){
                    throw new IllegalArgumentException("The instance number must be strictly positive.");
                }
            }
        }
        
        // Reads the number of customers
        int nCustomers = 1;
        if(cmd.hasOption("c")){
            String value = cmd.getOptionValue("c");
            if(value == null){
                throw new IllegalArgumentException("Please specify the number of customers.");
            }else{
                nCustomers = Integer.parseInt(value);
                if(nCustomers < 1){
                    throw new IllegalArgumentException("The number of customers must be strictly positive.");
                }
            }
        }
        
        // Reads the number of vehicles
        int nVehicles = 1;
        if(cmd.hasOption("v")){
            String value = cmd.getOptionValue("v");
            if(value == null){
                throw new IllegalArgumentException("Please specify the number of vehicles.");
            }else{
                nVehicles = Integer.parseInt(value);
                if(nVehicles < 1){
                    throw new IllegalArgumentException("The number of vehicles must be strictly positive.");
                }
            }
        }
        
        // Reads the city
        String city = "mi";
        if(cmd.hasOption("city")){
            String value = cmd.getOptionValue("city");
            if(value == null){
                throw new IllegalArgumentException("Please specify the city.");
            }else{
                city = value;
            }
        }
        
        // Checks whether customers have to be unique
        boolean identicalCustomers = false;
        if(cmd.hasOption("ic")){
            identicalCustomers = true;
        }
        
        // Checks whether to print information
        boolean verbose = false;
        if(cmd.hasOption("verb")){
            verbose = true;
        }
        
        
        // Reads the instance directory
        String instanceDirectory = "instances";
        if(cmd.hasOption("idir")){
            String value = cmd.getOptionValue("idir");
            if(value == null){
                throw new IllegalArgumentException("Please specify the directory of the instance files.");
            }else{
                instanceDirectory = value;
            }
        }
        
        
        // Stochastic Program
        // Reads the number of scenarios
        int nScenarios = 1;
        if(cmd.hasOption("s")){
            String value = cmd.getOptionValue("s");
            if(value == null){
                throw new IllegalArgumentException("Please specify the number of scenarios.");
            }else{
                nScenarios = Integer.parseInt(value);
                if(nScenarios < 1){
                    throw new IllegalArgumentException("The number of scenarios must be strictly positive.");
                }
            }
        }
        
        return new OptionValues(config,city,nVehicles,nCustomers,instance,identicalCustomers,
        instanceDirectory, verbose, nScenarios);
        
    }
    
    
    
}
