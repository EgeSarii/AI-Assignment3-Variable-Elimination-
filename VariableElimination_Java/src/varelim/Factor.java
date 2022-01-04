package varelim;

import java.util.ArrayList;

public class Factor {
    private Table table;
    private ArrayList<Variable> variables;
    private double prob ;

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
    public Factor (double probability)
    {
        String name = "Blank";
        ArrayList<String> possibleValues = new ArrayList<>();
        possibleValues.add("True");
        Variable newVariable = new Variable(name, possibleValues);
        ArrayList<Variable> variables = new ArrayList<>();
        variables.add(newVariable);
        this.variables = variables;
        ProbRow blankRow = new ProbRow(possibleValues, probability);
        ArrayList<ProbRow> newTable = new ArrayList<>();
        newTable.add(blankRow);
        this.table = new Table(newVariable, newTable);
        this.prob = probability;
    }

 /* 
  * =====================
  * Factor Operations
  * =====================
  */
    /**
     *  Factor production of two factors
     * @param f1 Factor f1 
     * @param f2 Factor f2
     * @return f3, the result of factor production for f1 and f2
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
            System.out.println(f2.getTable().toString() );
            System.out.println(f1.getTable().toString()+"\n" + f1.getProbability());
            
            throw new IllegalArgumentException("I am sorry but there are more than one common varibles!");
            
        }
        return f3;
    }
    /**
     *  Factor summation-out operation
     * @param f1 Factor f1
     * @param var Variable to be summed-out
     * @return f2 a new factor resulted by summation-out of f1
     */
    public static Factor marginalization(Factor f1, Variable var)
    {

        ArrayList<ProbRow> newTable = new ArrayList<>();
        ArrayList<Variable> allVariables= f1.getVariables();
        int varIndex = allVariables.indexOf(var);
        ArrayList <ProbRow> table = f1.getTable().getTable();
 
        while(!table.isEmpty())
        {
            ProbRow firstRow = table.remove(0);
            ArrayList<ProbRow> rowsDeleted = new ArrayList<>();
            ArrayList<ProbRow> rowList = new ArrayList<>();
            rowList.add(firstRow);
            for (int i= 0; i< table.size(); i++)
            {
                if(rowMatches(varIndex, firstRow, table.get(i)))
                {
                    rowList.add(table.get(i));
                    rowsDeleted.add(table.get(i));
                }
            }
            for(ProbRow r : rowsDeleted)
            {
                table.remove(r);
            }
           
            newTable.add(sumOutRow(varIndex, rowList));
            
        }
        allVariables.remove(var);
        if(allVariables.isEmpty())
        {
            double totalProb =0.0;
            for(ProbRow row : newTable)
            {
                totalProb += row.getProb();
            }
            Factor f2 = new Factor(totalProb);
            return f2;
        }
        Factor f2 = new Factor(allVariables, newTable);
        return f2;

    }
    /**
     * 
     * @param f1
     * @param var
     * @param val
     * @return
     */
    public static Factor reduction(Factor f1, Variable var)
    {
        ArrayList<Variable> variables = f1.getVariables();
        ArrayList<ProbRow> table = f1.getTable().getTable();
        ArrayList<ProbRow> newTable = new ArrayList<>(); 
        int varIndex = variables.indexOf(var);
        
        for( ProbRow row : table)
        {
            
            if(row.getValues().get(varIndex).equals( var.getObservedValue()))
            {
                row.getValues().remove(varIndex);
                newTable.add(row);

            }

        }
        variables.remove(varIndex);
        if(variables.isEmpty())
        {
            double totalProb =0.0;
            for(ProbRow row : newTable)
            {
                totalProb += row.getProb();
            }
            Factor f2 = new Factor(totalProb);
            return f2;
        }
        Factor f2 = new Factor(variables, newTable);
        return f2;
    }

/* 
  * ======================================
  * Factor Production Helper Operations
  * ======================================
  */

    /**
     * A method to get common variables of Factor f1 and Factor f2
     * @param f1 Factor f1
     * @param f2 Factor f2
     * @return commonVariables, a list of all common variables
     */
    private static ArrayList<Variable> getCommonVariables (Factor f1, Factor f2)
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
    private static ArrayList<Variable> getUnionVariables (Factor f1, Factor f2)
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
    private static ProbRow makeProductRow (ProbRow row1, ProbRow row2, int commonIndex)
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
    private static ProbRow makeProductRow (ProbRow row1, ProbRow row2)
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
  * ======================================
  * Factor Marginalization Helper Operations
  * ======================================
  */
   
    /**
     *  A helper function for sum-out operation. It sums the rows where the sum-out value is different
     * but other values are constant. Then it removes the sum-out value and return remaining values with
     * total probability.
     * @param varIndex index of the sum-out variable (and its value).
     * @param rowList list of rows to be summed.
     * @return new ProbRow, a new row built according to the sum-out operation
     */
    private static ProbRow sumOutRow (int varIndex, ArrayList<ProbRow> rowList)
    {
        ArrayList<String> values= new ArrayList<>();
        values = rowList.get(0).getValues();
        values.remove(varIndex);
        double prob =0.0;
        for(int i = 0; i< rowList.size(); i++)
        {
            prob = prob + rowList.get(i).getProb();
        }
        return new ProbRow(values, prob);
    } 
    /**
     * A helper function for sum-out operation. It checks if the rows matches or not. If the rows 
     * have same values for constant variables, and different values for the sum-out variable, they match.
     * @param varIndex Index of the sum-out variable (and its value).
     * @param row1 Row 1
     * @param row2 Row 2
     * @return (Row 1 matches Row 2)
     */
    private static Boolean rowMatches(int varIndex, ProbRow row1, ProbRow row2 )
    {
        ArrayList<String> values1 = row1.getValues();
        ArrayList<String> values2 = row2.getValues();

        for(int i = 0; i< values1.size(); i++)
        {
            if((i!= varIndex && !values1.get(i).equals(values2.get(i))) || 
               (i==varIndex && values1.get(i).equals( values2.get(i)) ))
            {
                return false;
            }
        }
        return true;
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

    public double getProbability()
    {
        return this.prob;
    }

}
