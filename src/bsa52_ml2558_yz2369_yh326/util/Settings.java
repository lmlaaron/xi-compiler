package bsa52_ml2558_yz2369_yh326.util;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Settings {

    
    // Supported optimizations
    public static List<String> supportedOpt = new ArrayList<String>(Arrays.asList(
            "reg", "cse", "cf", "copy", "dce"
            ));

    // Output options
    public static boolean lex = false;
    public static boolean parse = false;
    public static boolean typeCheck = false;
    public static boolean irgen = false;
    public static boolean irrun = false;
    public static Set<String> optIRSet = new HashSet<>();
    public static Set<String> optCFGSet = new HashSet<>();

    // Setting options
    public static String inputSourcePath = Paths.get(".").toAbsolutePath().toString();
    public static String outputPath = Paths.get(".").toAbsolutePath().toString();
    public static Set<String> libPath = new HashSet<>(Arrays.asList(Paths.get(".").toAbsolutePath().toString()));
    public static String assemblyOutputPath = Paths.get(".").toAbsolutePath().toString();
    public static String targetOS = "linux";
    public static Set<String> opts = new HashSet<>(supportedOpt);
    
    // File lists
    public static List<String> xiList = new ArrayList<>();
    public static List<String> ixiList = new ArrayList<>();

    // Internal usage options
    public static boolean asmComments = false;
    public static boolean genAbstract = false;
    public static boolean disAsmGen = false;
    public static boolean debugAst = false;
    public static boolean defaultValues = true;
}
