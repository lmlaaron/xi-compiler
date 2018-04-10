package bsa52_ml2558_yz2369_yh326.typecheck;

import java.io.FileWriter;
import java.io.IOException;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;

public class TypecheckerWrapper {
    /**
     * 
     * @param realInputFile,
     *            an absolute path to the input file
     * @param realOutputDir,
     *            an absolute path to the output directory
     */
    public static void WriteTypecheckingResult(String realOutputFile) {
        // typechecking
        try {
            FileWriter writer = new FileWriter(realOutputFile);
            writer.write("Valid Xi Program\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public static Node Typechecking(Node ast, String realInputFile, String libPath) throws Exception {
        SymbolTable sTable = new SymbolTable();
        ast.loadMethods(sTable, libPath);
        ast.typeCheck(sTable);
        return ast;
    }
}
