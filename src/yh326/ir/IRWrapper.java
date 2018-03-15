package yh326.ir;

import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.interpret.IRSimulator;
import yh326.ast.node.Node;
import yh326.exception.XiException;
import yh326.typecheck.TypecheckerWrapper;
import yh326.util.IRFuncDeclFinder;
/**
 * Wrapper for IR generation, canonicalization, and constant folding
 * @author lmlaaron
 *
 */
public class IRWrapper {
	
    /**
     * Generate the IR file
     */
	public static void IRGeneration(String realInputFile, String realOutputDir, 
			String fileName, String libPath, boolean optimization) {
		// generate the complete output path
        String outputFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".ir";
        String realOutputFile = Paths.get(realOutputDir, outputFileName).toString(); 
        
        try {
			FileWriter writer = new FileWriter(realOutputFile);
            try {
                IRNode irNode = getIRNode(realInputFile, fileName, libPath, optimization);
            	writer.write(irNode.toString());
	        } catch (XiException e) {
        		e.print(fileName);
        		writer.write(e.getMessage() + "\n");
	        } catch (Exception e) {
                e.printStackTrace();
            }
            writer.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return;
	}
	
	/*
	for testing purposes, will run both an optimized and non-optimized version
	of the generated IR code
	 */
	public static void IRRun(String realInputFile, String fileName, String libPath,
			boolean optimization) {
		try {
			IRNode irNode = getIRNode(realInputFile, fileName, libPath, optimization);

			List<IRFuncDecl> functions = findFuncDecls(irNode);

			if (functions.stream().noneMatch(decl -> (decl.name().equals("_Imain_paai")) )) {
				System.out.println("Can't run this file -- no _Imain_paai function found.");
				System.out.println("That file contains the following functions:");
				functions.stream().forEach(decl -> System.out.println('\t' + decl.name()) );
				return;
			}

			IRSimulator sim = new IRSimulator((IRCompUnit) irNode);
			sim.call("_Imain_paai"); // don't care about return value
		} catch (XiException e) {
    		e.print(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}

	private static List<IRFuncDecl> findFuncDecls(IRNode root) {
		IRFuncDeclFinder funcFinder = new IRFuncDeclFinder();
		root.visitChildren(funcFinder);
		return funcFinder.getFuncDecls();
	}
	
	public static IRNode getIRNode(String realInputFile, String fileName, String libPath,
			boolean optimization) throws Exception {
		Node ast = TypecheckerWrapper.getTypechecked(realInputFile, libPath);
        ast.fileName = fileName.substring(0, fileName.lastIndexOf("."));
        
        IRNode irNode = ast.translateProgram();
        //System.out.println(irNode.toString());
        irNode = Canonicalization.Canonicalize(irNode);
        irNode = Canonicalization.Lift(irNode);
        irNode = Canonicalization.TameCjump(irNode);
        if (optimization) {
        	irNode = Canonicalization.Folding(irNode);
        }
    	//System.out.println(irNode.toString());
        return irNode;
	}
}
