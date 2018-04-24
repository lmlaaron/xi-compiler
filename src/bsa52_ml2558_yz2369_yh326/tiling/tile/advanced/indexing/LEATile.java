package bsa52_ml2558_yz2369_yh326.tiling.tile.advanced.indexing;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;
import edu.cornell.cs.cs4120.xic.ir.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Tile uses LEA instruction to efficiently match:
 *
 * MEM ( [ADD/SUB] (TEMP, [CONST/TEMP])) and MEM ( [ADD/SUB] (TEMP, MUL (TEMP,
 * CONST)))
 */
public class LEATile extends Tile {
    /**
     * true if the matched tile is of the form [temp + temp*const]
     */
    protected boolean plus;
    /**
     * true if the matched tile is of the form [temp - temp*const]
     */
    protected boolean minus;

    protected AssemblyOperand addrOperand;

    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRMem) {
            IRMem mem = (IRMem) root;
            if (mem.expr() instanceof IRBinOp) {
                IRBinOp topBinop = (IRBinOp) mem.expr();
                if (topBinop.opType() == IRBinOp.OpType.ADD) {
                    this.plus = true;
                } else if (topBinop.opType() == IRBinOp.OpType.SUB) {
                    this.minus = true;
                }

                if (plus || minus) {
                    if (topBinop.left() instanceof IRTemp) {
                        IRTemp topTemp = (IRTemp) topBinop.left();
                        if (topBinop.right() instanceof IRBinOp) {
                            IRBinOp bottomBinop = (IRBinOp) topBinop.right();
                            if (bottomBinop.opType() == IRBinOp.OpType.MUL) {
                                if (bottomBinop.left() instanceof IRTemp && bottomBinop.right() instanceof IRConst) {
                                    IRTemp temp = (IRTemp) bottomBinop.left();
                                    IRConst cnst = (IRConst) bottomBinop.right();

                                    this.subtreeRoots = new LinkedList<>(); // no subtrees, this tile matches a leaf

                                    String[] parts = new String[] { topTemp.name(), temp.name(),
                                            Long.toString(cnst.constant()) };

                                    if (plus) {
                                        this.addrOperand = AssemblyOperand.MemPlus(parts);
                                        return true;
                                    } else if (minus) {
                                        this.addrOperand = AssemblyOperand.MemMinus(parts);
                                        return true;
                                    }
                                }
                            }

                        } else if (topBinop.right() instanceof IRConst) {
                            IRConst cnst = (IRConst) topBinop.right();

                            String[] parts = new String[] { topTemp.name(), Long.toString(cnst.value()) };

                            if (plus) {
                                this.addrOperand = AssemblyOperand.MemPlus(parts);
                                return true;
                            }
                            if (minus) {
                                this.addrOperand = AssemblyOperand.MemMinus(parts);
                                return true;
                            }
                        } else if (topBinop.right() instanceof IRTemp) {
                            IRTemp tmp = (IRTemp) topBinop.right();

                            String[] parts = new String[] { topTemp.name(), tmp.name() };

                            if (plus) {
                                this.addrOperand = AssemblyOperand.MemPlus(parts);
                                return true;
                            }
                            if (minus) {
                                this.addrOperand = AssemblyOperand.MemMinus(parts);
                                return true;
                            }
                        }
                    }
                }
            }

        }

        // reset values if didn't return true
        plus = false;
        minus = false;
        addrOperand = null;

        return false;
    }

    @Override
    public int size() {
        return 6; // technically the answer is "six or four", but no way to express that
    }

    @Override
    protected Assembly generateLocalAssembly() {
        LinkedList<AssemblyStatement> statements = new LinkedList<>();

        String freshTemp = freshTemp();

        statements.add(new AssemblyStatement("lea", new AssemblyOperand(freshTemp)/*new AssemblyOperand("rsi")*/, addrOperand));

        return new Assembly(statements, AssemblyOperand.MemPlus(freshTemp)/*new AssemblyOperand("[rsi]")*/);
    }
}
