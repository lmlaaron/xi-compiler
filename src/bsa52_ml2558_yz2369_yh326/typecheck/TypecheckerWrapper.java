package bsa52_ml2558_yz2369_yh326.typecheck;

import java.io.FileWriter;
import java.io.IOException;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.util.Settings;

public class TypecheckerWrapper {
    public static Node Typechecking(Node ast, String outputFile) throws Exception {
        SymbolTable sTable = new SymbolTable();
        ast.loadMethods(sTable, Settings.libPath);
        ast.typeCheck(sTable);
        if (Settings.typeCheck) 
            TypecheckerWrapper.WriteTypecheckingResult(outputFile + ".typed");
        return ast;
    }
    
    /**
     * 
     * @param realInputFile,
     *            an absolute path to the input file
     * @param realOutputDir,
     *            an absolute path to the output directory
     */
    public static void WriteTypecheckingResult(String outputFile) {
        // typechecking
        try {
            FileWriter writer = new FileWriter(outputFile);
            writer.write("Valid Xi Program\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }


}
