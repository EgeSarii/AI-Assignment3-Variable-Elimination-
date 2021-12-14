package varelim;

import java.util.ArrayList;

public class Factor {
    private Table table;
    private Variable variable;
    private ArrayList<Variable> parents;

    public Factor (Table table)
    {
        this.table = table;
        this.variable = table.getVariable();
        this.parents = table.getParents();
    }

 /* 
  * =====================
  * Factor Operations
  * =====================
  */

    public Factor production (Factor f1, Factor f2)
    {
        return f1;
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

    public Variable getVariable()
    {
        return this.variable;
    }

    public ArrayList<Variable> getParents()
    {
        return this.parents;
    }
    
    
    

}
