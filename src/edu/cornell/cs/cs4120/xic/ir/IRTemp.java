package edu.cornell.cs.cs4120.xic.ir;

import edu.cornell.cs.cs4120.util.SExpPrinter;

/**
 * An intermediate representation for a temporary register
 * TEMP(name)
 */
public class IRTemp extends IRExpr_c {
    private String name;

    /**
     *
     * @param name name of this temporary register
     */
    public IRTemp(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String label() {
        return "TEMP(" + name + ")";
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("TEMP");
        try {
            p.printAtom(name);
        }
        catch (NullPointerException npe) {
            p.printAtom("NULL!");
        }
        p.endList();
    }
}
