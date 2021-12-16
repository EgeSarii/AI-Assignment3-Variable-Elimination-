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
        if(table.getVariable().hasParents())
        {
            variables.addAll(table.getParents());
        }
        
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
        ArrayList<ProbRow> newTable = new ArrayList<>();
        if(commonVariables.size()==0)
        {
            for(int i=0; i< f1.table.size(); i++ )
            {
                ProbRow prowf1 = f1.table.get(i);
                for(int j=0; j<f2.table.size(); j++)
                {
                    ProbRow prowf2 = f2.table.get(j);
                    ProbRow newRow = makeProductRow(prowf1, prowf2);
                    newRow.getValues().size();
                    newTable.add(newRow);
                }
            }
            f3 = new Factor(getUnionVariables(f1, f2), newTable);
        }
        else if (commonVariables.size() ==1)
        {
            Variable common = commonVariables.get(0);
            int commonIndexf1 = f1.variables.indexOf(common);
            int commonIndexf2 = f2.variables.indexOf(common);
            
            
            for(int i=0; i< f1.table.size(); i++ )
            {
                ProbRow prowf1 = f1.table.get(i);
                for(int j=0; j<f2.table.size(); j++)
                {
                    ProbRow prowf2 = f2.table.get(j);
                    if(prowf1.getValues().get(commonIndexf1).equals(prowf2.getValues().get(commonIndexf2)))
                    {
                        ProbRow newRow = makeProductRow(prowf1, prowf2, commonIndexf2);
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

    /**
     * A method to get common variables of Factor f1 and Factor f2
     * @param f1 Factor f1
     * @param f2 Factor f2
     * @return commonVariables, a list of all common variables
     */
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
    
    /**
     * A method to get the union of variable lists of two factors
     * @param f1 Factor f1
     * @param f2 Factor f2
     * @return unionVariables, a union variable list of variables of f1 and f2
     */
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

    /**
     *  A method to make a row production with common variable
     * @param row1 Row 1 
     * @param row2 Row 2
     * @param commonIndex The index of the common variable in Row 2
     * @return newRow, a new row that production of Row 1 and Row 2 regarding the common variable.
     */
    public static ProbRow makeProductRow (ProbRow row1, ProbRow row2, int commonIndex)
    {
        ArrayList<String> newValues = new ArrayList<>();
        newValues.addAll(row1.getValues());
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
    
    /**
     *  A method to make a row production without common variable
     * @param row1 Row 1 
     * @param row2 Row 2
     * @return newRow, a new row that production of Row 1 and Row 2 
     */
    public static ProbRow makeProductRow (ProbRow row1, ProbRow row2)
    {
        ArrayList<String> newValues = new ArrayList<>();
        newValues.addAll(row1.getValues()); 
        for(int i =0; i< row2.getValues().size(); i++ )
        {
            newValues.add(row2.getValues().get(i));    
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
