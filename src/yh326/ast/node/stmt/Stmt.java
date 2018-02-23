/**
 * author: ml2558
 **/
package yh326.ast.node.stmt;
import yh326.ast.node.Expr;
import yh326.ast.node.Node;
import yh326.ast.node.NodeDecoration;
import yh326.ast.type.*; //TODO should it be ast.node.type?


public class Stmt extends Node {

}










/*
public class PRCALLUNIT extends Stmt {
     String identifier;
     Node Expr_list;
     
     @Override
     public PRCALLUNIT(Node... Nodes) {
         this.decoration = new NodeDecoration();
         this.value = null;
         this.children = new ArrayList<Node>();
     }
     @Override
     public NodeType typeCheck(SymbolTable st) throws TypeErrorException {
        try { 
            st.checkVariableType(identifier, );   
        } catch (Exception e) {
            throw new TypeErrorException(dd,);
        }
        return new VarType(PrimitiveTypeNode.UNIT);
     }
}

public class PRCALL extends Stmt {
}

public class PRCALLMULTI extends Stmt {
}

public class RETURN extends Stmt {
}

public class RETVAL extends Stmt {
}

public class RETMULTI extends Stmt {
} 
*/
