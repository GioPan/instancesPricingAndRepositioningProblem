/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author lct495
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws org.apache.commons.cli.ParseException
     */
    public static void main(String[] args) throws FileNotFoundException, ParseException {
        
        // Reads command line arguments
        CommandLineInterpreter cli = new CommandLineInterpreter();
        OptionValues opts = cli.parse(args);        
        
        PricingProblem pp = InstanceGenerator.generate(opts.getInstanceDirectory(),
                opts.getConfig(),opts.getCity(),opts.getInstanceNumber(),
                opts.getnVehicles(),opts.getnCustomers(),opts.getnScenarios(),opts.identicalCustomers());
        if(opts.verbose()){
            pp.print();
        }
        RandomUtility ru = InstanceGenerator.generateRandomUtility(pp, opts.getInstanceNumber());
        
        //double scenario[][] = new double[pp.getnCustomers()][pp.getnServices()];
        List<double[][]> scenarios = new ArrayList();

        for(int s = 1; s <= pp.getnScenarios(); s++){
            scenarios.add(s-1, new double[pp.getnCustomers()][pp.getnServices()]);
            // It generats a random utility scenario. The random utility is the same for all shared cars.
            for(int k = 1; k <= pp.getnCustomers(); k++){
                double uCS = ru.sample();
                for(int v = 1; v <= pp.getnServices(); v++){
                    if(pp.isAlternative(v)){
                        scenarios.get(s-1)[k-1][v-1] = ru.sample();
                    }else{
                        scenarios.get(s-1)[k-1][v-1] = uCS;
                    }
                }
            }
        }
        
        
        
    }
    
}
