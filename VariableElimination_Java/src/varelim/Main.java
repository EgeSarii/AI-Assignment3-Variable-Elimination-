package varelim;


import java.io.IOException;
import java.net.CacheRequest;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.namespace.QName;

/**
 * Main class to read in a network, add queries and observed variables, and run variable elimination.
 * 
 * @author  Ege Sari, Marcel de Korte, Moira Berens, Djamari Oetringer, Abdullahi Ali, Leonieke van den Bulk
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
		
		// Print the query and observed variables
		ui.printQueryAndObserved(query, observed);
		

		//PUT YOUR CALL TO THE VARIABLE ELIMINATION ALGORITHM HERE
		variableElimination (query, observed, vs, ps );		
	}

	public static void variableElimination (Variable query, ArrayList<Variable> observed, ArrayList<Variable> variables, ArrayList<Table> probabilities)
	{
		//Setting the log file
		Logger logger = Logger.getLogger("LogVE");
		FileHandler fh; 
		try {  

			// This block configure the logger with handler and formatter  
			fh = new FileHandler("/home/ege/ai-assignment3-variable-elimination/LogVe.log");  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  
	
			// the following statement is used to log any messages  
			logger.info("The Algorithm starts");  
	
		} catch (SecurityException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  


		//creating factors by using reduction if there is an observed variable
		ArrayList<Factor> factors = new ArrayList<>();
		logger.info("The initial factor list, its empty : " + factors);
		for (Table table : probabilities)
		{
			Factor newFactor = new Factor(table);
			for (Variable obs : observed)
			{
				if (newFactor.getVariables().contains(obs))
				{	
					newFactor = Factor.reduction(newFactor, obs);
					logger.info("The reduced factor is added : \n" +newFactor.getTable().toString() );
				}
			}
			if(newFactor.getProbability() !=1.0)
			{
				factors.add (newFactor);
			}
			logger.info("Non-reduced factor is added : \n" + newFactor.getTable().toString());
		}

		//Make a list of variables to be summed-out
		ArrayList<Variable> varSumList = new ArrayList<>();
		
		variables.remove(query);

		//List of variables to be summed-out
		varSumList = variables;
		String varList = "";
		for(Variable vrbls : varSumList)
			{
				varList = (varList + ", "+ vrbls.getName());
			}
		logger.info("The list of variables to be summed-out : " +varList );
		
		//List of factors that contain Variable Z

		for(Variable var :varSumList)
		{
			
			ArrayList<Factor> containsVarList = new ArrayList<>();
			logger.info("The list of factors containing variable " + var.getName()+" for initially :" + containsVarList);
			//find the factors contain Variable var and add to the list
			for(Factor f : factors)
			{
				if(f.getVariables().contains(var))
				{
					containsVarList.add(f);
					logger.info("The list of factors containing variable " + var.getName()+ " : \n" +f.getTable().toString());
					
				}
			}
			factors.removeAll(containsVarList);
			String facList = "";
			for(Factor fctr : factors)
			{
				facList = (facList + "\n " +   (fctr.getTable().toString()));
			}
			logger.info("The list of factors to be multiplied : \n" +facList);
			if(!containsVarList.isEmpty())
			{
				Factor newFactor = Factor.marginalization(multiply(containsVarList), var);
				logger.info("The factor after it the list is multiplied :\n" + newFactor.getTable().getTable());
				if(newFactor.getProbability()!=1.0 )
				{
					factors.add(newFactor);
					String facList1 = "";
					for(Factor fctr : factors)
					{
					facList1 = (facList + "\n " +   (fctr.getTable().toString()));
					}
					logger.info("The list of factors after the operation is done for one variable :\n" + facList1 );
				}
			}

			
		}
		
		logger.info("The elimination is over. Only one factor left \n" + factors.get(0).getTable().toString());

		//Unfortunately the normalization does not work correctly.
		//System.out.println(normalize(factors.get(0)).getTable().toString());
	}

	/**
	 * A helper function for variable elimination. It applies product operation on the list of factors
	 * @param factors Factors to be multiplied
	 * @return multiplication of factors.
	 */
	private static Factor multiply (ArrayList<Factor> factors)
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
	/**
	 * A helper function for variable elimination. It normalizes the factor
	 * @param f Factor to be normalized
	 * @return normalized factor
	 */
	private static Factor normalize (Factor f)
	{
		Factor summedOut = Factor.marginalization(f, f.getVariables().get(0));
		ArrayList<ProbRow> newTable = new ArrayList<>();
		for(ProbRow row : f.getTable().getTable())
		{	
			ProbRow newRow = new ProbRow(row.getValues(), (row.getProb() / summedOut.getProbability()));
			newTable.add(newRow);
		}
		ArrayList<Variable> temp = new ArrayList<>();
		temp.add(f.getTable().getVariable());
		Factor normalized = new Factor(temp, newTable);
		return normalized;

	}
}