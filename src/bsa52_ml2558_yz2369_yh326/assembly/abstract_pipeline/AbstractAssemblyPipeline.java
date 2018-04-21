package bsa52_ml2558_yz2369_yh326.assembly.abstract_pipeline;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;

/**
 * represents the sequence of steps involved in converting abstract assembly
 * into real instructions
 */
public interface AbstractAssemblyPipeline {
    Assembly process(Assembly abstractAssembly);
}
