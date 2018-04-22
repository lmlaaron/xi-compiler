package bsa52_ml2558_yz2369_yh326.assembly.abstract_pipeline;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.optimization.register_allocation.RegisterAllocation;

public class OptimizedRegisterAllocationPipeline implements AbstractAssemblyPipeline {
    @Override
    public Assembly process(Assembly abstractAssembly) {
        RegisterAllocation.RegisterAllocation(abstractAssembly);
        return abstractAssembly;
    }
}
