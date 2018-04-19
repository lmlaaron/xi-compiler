package bsa52_ml2558_yz2369_yh326.util.graph.Node;

import bsa52_ml2558_yz2369_yh326.util.NumberGetter;

public class CFGNode<T> {
    public String id;
    public T data;
    
    public CFGNode(T data) {
        this.id = "n" + NumberGetter.uniqueNumber();
        this.data = data;
    }
}
