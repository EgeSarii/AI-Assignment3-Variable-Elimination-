package varelim;

import java.time.chrono.ThaiBuddhistChronology;
import java.util.ArrayList;

public class Factor {
    private Table table;
    private ArrayList<Variable> variables;

    /**
     * Constructor of the Factor.
     * @param variables
     * @param table
     */
    public Factor (ArrayList<Variable> variables, ArrayList<ProbRow> table)
    {
        this.variables = variables;
        Variable temp = variables.get(0);
        ArrayList<Variable> parents = new ArrayList<>();
        for(int i =1; i< variables.size(); i++)
        {
            parents.add(variables.get(i));
        }
        temp.setParents(parents);
        this.table = new Table(temp, table);
    }
    /**
     * Constructor of the Factor.
     * @param table
     */
    public Factor (Table table)
    {
        this.table = table;
        ArrayList <Variable>variables = new ArrayList<>();
        variables.add(table.getVariable());
        variables.addAll(table.getParents());
        this.variables = variables;
    }

 /* 
  * =====================
  * Factor Operations
  * =====================
  */

    public static Factor production (Factor f1 , Factor f2)
    {
        Factor f3;
        //Find the common variable(s)
        ArrayList<Variable> commonVariables = getCommonVariables(f1, f2);

        if(commonVariables.size()==0)
        {
            throw new IllegalArgumentException("No common variables!");
        }
        else if (commonVariables.size() ==1)
        {
            Variable common = commonVariables.get(0);
            int commonIndexf1 = f1.variables.indexOf(common);
            int commonIndexf2 = f2.variables.indexOf(common);
            
            ArrayList<ProbRow> newTable = new ArrayList<>();
            for(int i=0; i< f1.table.size(); i++ )
            {
                ProbRow prowf1 = f1.table.get(i);
                for(int j=0; j<f2.table.size(); j++)
                {
                    ProbRow prowf2 = f2.table.get(j);
                    if(prowf1.getValues().get(commonIndexf1).equals(prowf2.getValues().get(commonIndexf2)))
                    {
                        ProbRow newRow = makeUnionRow(prowf1, prowf2, commonIndexf2);
                        newTable.add(newRow);
                    }
                }
            }
            f3 = new Factor(getUnionVariables(f1, f2), newTable);
        }
        else
        {
            throw new IllegalArgumentException("I am sorry but there are more than one common varibles!");
        }
        return f3;
    }

    public static ArrayList<Variable> getCommonVariables (Factor f1, Factor f2)
    {
        ArrayList<Variable> commonVariables = new ArrayList<>();
        for (Variable v : f1.variables) 
        {
            if (f2.variables.contains(v)) 
            {
                commonVariables.add(v);
            }
        }   
        return commonVariables;
    }
    public static ArrayList<Variable> getUnionVariables (Factor f1, Factor f2)
    {
        ArrayList<Variable> variablesToBeAdded = new ArrayList<>();
        for (Variable v : f2.variables) 
        {
            if (! f1.variables.contains(v)) 
            {
                variablesToBeAdded.add(v);
            }
        }   
        ArrayList<Variable> unionVariables = f1.variables;
        unionVariables.addAll(variablesToBeAdded);
        return unionVariables;
    }

    public static ProbRow makeUnionRow (ProbRow row1, ProbRow row2, int commonIndex)
    {
        ArrayList<String> newValues = row1.getValues(); 
        for(int i =0; i< row2.getValues().size(); i++ )
        {
            if(i!= commonIndex)
            {
                newValues.add(row2.getValues().get(i));
            }
        }
        ProbRow newRow = new ProbRow(newValues, (row1.getProb() * row2.getProb()));
        return newRow;
    }

 /* 
  * =====================
  * Getter & Setter Functions
  * =====================
  */

    public Table getTable()
    {
        return this.table;
    }

    public ArrayList<Variable> getVariables()
    {
        return this.variables;
    }

}
