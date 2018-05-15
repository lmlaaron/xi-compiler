package bsa52_ml2558_yz2369_yh326.util;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

public class TempRenamer extends IRVisitor {

    /**
     * names which hold special significance during the translation process
     */
    static String[] specialPrefixes = new String[] { "_ARG", "_RET" };

    String suffix;

    public TempRenamer(String suffix) {
        super(new IRNodeFactory_c());
        this.suffix = suffix;
    }

    protected boolean specialName(String name) {
        for (String prefix : specialPrefixes) {
            if (name.length() < prefix.length())
                continue;
            if (name.substring(0, prefix.length()).equals(prefix))
                return true;
        }
        return false;
    }

    @Override
    protected IRVisitor enter(IRNode parent, IRNode n) {
        if (n instanceof IRTemp) {
            IRTemp tmp = (IRTemp) n;
            if (!specialName(tmp.name()) && !tmp.name().endsWith(suffix) && tmp.name().charAt(0)!='_') {
                tmp.setName(tmp.name() + suffix);
            }
        }

        return this;
    }
}
