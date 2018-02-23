/**
 * author: ml2558
 **/
package yh326.ast.node;
import yh326.ast.type.*; //TODO should it be ast.node.type?
public class Stmt extends Node {

}

public class SEQ extends Stmt {
   protected List<Stmt> stmt_seq;
   @Override
   public SEQ(Nodes... Nodes) {
         this.decoration = new NodeDecoration();
         this.value = null;
         this.children = new ArrayList<Node>();
         for (Node node : nodes) {
             this.children.add(node);
         }
   } 

   @Override 
   public Type typeCheck(symbolTable st) throws TypeErrorException {
       Type retT = null;
       for (Node node : nodes) {
           retT = node.typeCheck(st);
       }
       return retT;
   }

}

public class EMPTY extends Stmt {
   @Override
   public Type typeCheck() throws TypeErrorException {
       if (this.value=="{}") {
           return new VarType(PrimitiveType.UNIT);
       }      
   }
}

public class IF extends Stmt {
    protected Expr guard;
    protected Stmt consequent;

    @Override
    public IF(Node... nodes) {
         this.decoration = new NodeDecoration();
         this.value = null;
         this.children = new ArrayList<Node>();
         for (Node node : nodes) { 
             this.children.add(node);
         }
         if (nodes[0] instanceof Expr && nodes[1] instanceof Stmt) {
            guard = nodes[0];
            consequent = nodes[1];
         }
    }

    @Override
    public Type typeCheck(SymbolTable st) throws TypeErrorException {
         Type tg = guard.typeCheck(st);
         if (!tg.equals(new VarType(PrimitiveType.BOOL))) {
             throw new TypeErrorException(PrimitiveType.BOOL, tg);
         }
         Type tc = consequent.typeCheck(st);
         return new VarType(PrimitiveType.UNIT);
    }
}

public class IFELSE extends Stmt {
    protected Expr guard;
    protected Stmt consequent;
    protected Stmt alternative;

    @Override
    public IFELSE(Node... nodes) {
         this.decoration = new NodeDecoration();
         this.value = null;
         this.children = new ArrayList<Node>();
         for (Node node : nodes) { 
             this.children.add(node);
         }
         if (nodes[0] instanceof Expr && nodes[1] instanceof Stmt && nodes[2] instanceof Stmt) {
            guard = nodes[0];
            consequent = nodes[1];
            alternative = nodes[2];
         }
    }

    @Override
    public Type typeCheck(SymbolTable st) throws TypeErrorException {
         Type tg = guard.typeCheck(st);
         if (!tg.equals(new VarType(PrimitiveType.BOOL))) {
             throw new TypeErrorException(PrimitiveType.BOOL, tg);
         }
         Type tc = consequent.typeCheck(st);
         Type ta = alternative.typeCheck(st);
         return new Type.Lub(tc, ta);
    }
}

public class WHILE extends Stmt {
    protected Expr guard;
    protected Stmt consequent;

    @Override
    public WHILE(Node... nodes) {
         this.decoration = new NodeDecoration();
         this.value = null;
         this.children = new ArrayList<Node>();
         for (Node node : nodes) { 
             this.children.add(node);
         }
         if (nodes[0] instanceof Expr && nodes[1] instanceof Stmt) {
            guard = nodes[0];
            consequent = nodes[1];
         }
    }

    @Override
    public Type typeCheck(SymbolTable st) throws TypeErrorException {
         Type tg = guard.typeCheck(st);
         if (!tg.equals(new VarType(PrimitiveType.BOOL))) {
             throw new TypeErrorException(PrimitiveType.BOOL, tg);
         }
         Type tc = consequent.typeCheck(st);
         return new VarType(PrimitiveType.UNIT);
    }
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
     public Type typeCheck(SymbolTable st) throws TypeErrorException {
        try { 
            st.checkVariableType(identifier, );   
        } catch (Exception e) {
            throw new TypeErrorException(dd,);
        }
        return new VarType(PrimitiveType.UNIT);
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
