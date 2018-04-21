package bsa52_ml2558_yz2369_yh326.dataflow_analysis;

import bsa52_ml2558_yz2369_yh326.util.graph.Graph;

import java.util.Map;

public class DataflowAnalysisResult<GT, IT> {
    public Graph<GT> cfg;
    public Map<GT, IT> in;
    public Map<GT, IT> out;

    public DataflowAnalysisResult(Graph<GT> cfg, Map<GT, IT> in, Map<GT, IT> out) {
        this.cfg = cfg;
        this.in = in;
        this.out = out;
    }
}
