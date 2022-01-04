package varelim;


import java.net.CacheRequest;
import java.util.ArrayList;
import javax.xml.namespace.QName;

/**
 * Main class to read in a network, add queries and observed variables, and run variable elimination.
 * 
 * @author Marcel de Korte, Moira Berens, Djamari Oetringer, Abdullahi Ali, Leonieke van den Bulk
 */

public class Main {
	private final static String networkName = "earthquake.bif"; // The network to be read in (format and other networks can be found on http://www.bnlearn.com/bnrepository/)

	public static void main(String[] args) {
		
		// Read in the network
		Networkreader reader = new Networkreader(networkName); 
		
		// Get the variables and probabilities of the network
		ArrayList<Variable> vs = reader.getVs(); 
		ArrayList<Table> ps = reader.getPs(); 
		
		// Make user interface
		UserInterface ui = new UserInterface(vs, ps);
		
		// Print variables and probabilities
		ui.printNetwork();
		// Ask user for query
		ui.askForQuery(); 
		Variable query = ui.getQueriedVariable(); 
		
		// Ask user for observed variables 
		ui.askForObservedVariables(); 
		ArrayList<Variable> observed = ui.getObservedVariables();

		
		variableElimination (query, observed, vs, ps );		 
	/* 
		// Ask user for query
		ui.askForQuery(); 
		Variable query = ui.getQueriedVariable(); 
		
		// Ask user for observed variables 
		//ui.askForObservedVariables(); 
		ArrayList<Variable> observed = ui.getObservedVariables();
		 
		
		// Turn this on if you want to experiment with different heuristics for bonus points (you need to implement the heuristics yourself)
//		ui.askForHeuristic();
//		String heuristic = ui.getHeuristic();
		
		// Print the query and observed variables
		ui.printQueryAndObserved(query, observed);
		*/
		//PUT YOUR CALL TO THE VARIABLE ELIMINATION ALGORITHM HERE
	}

	public static void variableElimination (Variable query, ArrayList<Variable> observed, ArrayList<Variable> variables, ArrayList<Table> probabilities)
	{
		//creating factors by using reduction if there is an observed variable
		ArrayList<Factor> factors = new ArrayList<>();
		for (Table table : probabilities)
		{
			Factor newFactor = new Factor(table);
			for (Variable obs : observed)
			{
				if (newFactor.getVariables().contains(obs))
				{
					newFactor = Factor.reduction(newFactor, obs);
					newFactor.getTable().toString();
				}
			}
			factors.add (newFactor);
		}

		//Make a list of variables to be summed-out
		ArrayList<Variable> varSumList = new ArrayList<>();
		variables.remove(query);

		//List of variables to be summed-out
		varSumList = variables;

		//List of factors that contain Variable Z

		for(Variable var :varSumList)
		{
			
			ArrayList<Factor> containsVarList = new ArrayList<>();
			//find the factors contain Variable var and add to the list
			for(Factor f : factors)
			{
				if(f.getVariables().contains(var))
				{
					containsVarList.add(f);
				}
			}
			factors.removeAll(containsVarList);
			for(Factor f : containsVarList)
			{
			//	System.out.println(f.getTable().toString() +" \n");
			}
			Factor facttttoorrr = multiply(containsVarList);
			//System.out.println(facttttoorrr.getTable().toString() + " \n");
			Factor newFactor = Factor.marginalization(facttttoorrr, var);
			//System.out.println(newFactor.getTable() +" \n");

			if(newFactor.getProbability()!=1.0 )
			{
				factors.add(newFactor);
			}
		}
		
		System.out.println(factors.get(0).getTable().toString());
	}

	public static Factor multiply (ArrayList<Factor> factors)
	{
		ArrayList<Factor> factorsToBeRemoved = new ArrayList<>();
		if(factors.size() ==1)
		{
			return factors.get(0);
		}
		else
		{
			Factor result = Factor.production(factors.get(0), factors.get(1));
			factorsToBeRemoved.add(factors.get(0));
			factorsToBeRemoved.add(factors.get(1));
			factors.removeAll(factorsToBeRemoved);
			factors.add(result);
			return multiply(factors);
		}

	}
}