package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

public class CompUnitTile extends Tile {
	public Map<String, Integer>  global_variables;
    public Map<String, Integer>  global_variables_init;
	
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRCompUnit) {
            this.root = root;
            this.global_variables=new LinkedHashMap<String,Integer>(((IRCompUnit) root).global_variables);
            this.global_variables_init=new LinkedHashMap<String,Integer>(((IRCompUnit) root).global_variables_init);
            IRCompUnit cu = (IRCompUnit) root;


            this.subtreeRoots = new LinkedList<>();
            for (IRFuncDecl decl : cu.functions().values())
                subtreeRoots.add(decl);
            
            return true;
        } else
            return false;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    protected Assembly generateLocalAssembly() {
        LinkedList<AssemblyStatement> statements = new LinkedList<>();
        // assembly annotations for function
        statements.add(new AssemblyStatement(".data "));
        
        for (String name: global_variables.keySet()) {
        		statements.add(new AssemblyStatement(".globl " + name));
        		statements.add(new AssemblyStatement(".align 4"));
    			if ( global_variables.get(name) > 1) {
    				statements.add(new AssemblyStatement(".quad " + String.valueOf(global_variables.get(name))));
    			}
        		statements.add(new AssemblyStatement(name+":", true));
        		if (global_variables.containsKey(name)) {
        			int i = 0;

        			for (i = 0; i <global_variables.get(name); i++) {
        				if (global_variables_init != null && global_variables_init.get(name) != null) {
        					statements.add(new AssemblyStatement(".quad "+ String.valueOf(global_variables_init.get(name))));
        				} else {
        					statements.add(new AssemblyStatement(".quad 0"));
        				}
        			}
        		} else {
        			int i = 0;
        			for (i = 0; i <global_variables.get(name); i++) {
        				statements.add(new AssemblyStatement(".quad 0"));
        			}
        		}
        }
        return new Assembly(statements);
    }
}
