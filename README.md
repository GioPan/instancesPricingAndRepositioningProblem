# Instance generator
This repository contains a minimal generator for the instances of the Carsharing Pricing and Relocation problem discussed in the article G. Pantuso, "Exact solutions to a carsharing pricing and relocation problem". The instance generator is written in Java and is thoroughly described in the article.
Particularly this repository contains two folders: The [src](./src) folder, which contains the Java code, and the [instances](./instances) folder, which contains the data files processed by the code to generate instances. 
# How to generate instances
In order to generate instances the code accepts the following command line arguments
-- -v the number of vehicles (required)
-- -c the number of customers (required)
-- -s the number of scenarios (optional, default = 1)
-- -conf the configuration file numbe (optional, default = 0). This represents the configuration file in the instance folder (default `config_0.txt`).
-- -i the instance number (optional, default = 1). This number represents the seed of the random number generators used in several places. Different seeds allow generating (and replicating) different instances with the same characteristics (number of vehicles, customers and so on). For example, the vehicles and customers will be distributed in a different way and the scenarios will be different. 
-- city the city for which we are reading the data (optional, default = "mi"). This determines which files are read (default `zones_mi.txt`and `times_mi.txt`).
-- ic whether customers should be characterized by the same utility function (default = false). By passing the options the customers will be identical. 
-- verb whether to print instance information (default = false). 
Example usage 
-- `java Main -v 10 -c 50 -s 10 -conf 0 -i 1 -verb ` creates an instance with 10 vehicles, 50 customers and 10 scenarios using configuration file `config_0.txt`and seed $1$. Prints information on the instance generated. 
-- `java Main -v 10 -c 50 -verb ` creates an instance with 10 vehicles, 50 customers and 1 scenario (defaults) using configuration file `config_0.txt` (default) and seed $1$ (default). Prints information on the instance generated. 

The code produced an object of the class [PricingProblem](./src/PricingProblem.java). Such object, which represents an instance of the problem, contains a number of utility methods to extract all the instance's information. In addition it creates the desired number of customer utility scenarios (see the [Main file](./src/Main.java). The user can extend and modify the code to export instances information as required. 

# Dependencies
The code is dependent on the following libraries:
-- [Apache Commons Math 3.4.1](https://commons.apache.org/proper/commons-math/download_math.cgi)
-- [Apache Commons CLI 1.2](https://commons.apache.org/proper/commons-cli/)
