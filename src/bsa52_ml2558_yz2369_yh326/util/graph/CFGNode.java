package bsa52_ml2558_yz2369_yh326.util.graph;

import bsa52_ml2558_yz2369_yh326.util.NumberGetter;

public class CFGNode {
    public String id;
    public String label;
    
    public CFGNode(String label) {
        this.id = "n" + NumberGetter.uniqueNumber();
        this.label = label;
    }
}
