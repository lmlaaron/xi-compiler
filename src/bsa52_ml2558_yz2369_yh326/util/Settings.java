package bsa52_ml2558_yz2369_yh326.util;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Settings {

    public static boolean brentHack = false;

    // Output options
    public static boolean lex = false;
    public static boolean parse = false;
    public static boolean typeCheck = false;
    public static boolean irgen = false;
    public static boolean irrun = false;
    public static Set<String> optIRList = new HashSet<String>();
    public static Set<String> optCFGList = new HashSet<String>();

    // Setting options
    public static String inputSourcePath = Paths.get(".").toAbsolutePath().toString();
    public static String outputPath = Paths.get(".").toAbsolutePath().toString();
    public static String libPath = Paths.get(".").toAbsolutePath().toString();
    public static String assemblyOutputPath = Paths.get(".").toAbsolutePath().toString();
    public static String targetOS = "linux";
    public static boolean optimization = true;
    public static Set<String> opts = new HashSet<String>();
    public static Set<String> noOpts = new HashSet<String>();

    // File lists
    public static List<String> xiList = new ArrayList<String>();
    public static List<String> ixiList = new ArrayList<String>();

    // Internal usage options
    public static boolean asmComments = false;
    public static boolean genAbstract = false;
    public static boolean disAsmGen = false;
}
