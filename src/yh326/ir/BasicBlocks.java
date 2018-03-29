package yh326.ir;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.IRCJump;
import edu.cornell.cs.cs4120.xic.ir.IRJump;
import edu.cornell.cs.cs4120.xic.ir.IRLabel;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import yh326.util.NumberGetter;

/**
 * 
 * @author lmlaaron
 * @collection of basicblocks
 */
public class BasicBlocks {

    // class for one single basicblock
    class BasicBlock {
        public List<IRStmt> stmts;
        public List<BasicBlock> successors;

        public boolean marked;

        public boolean isMarked() {
            return marked;
        }

        public void mark() {
            marked = true;
        }

        public BasicBlock(List<IRStmt> st) {
            stmts = st;
            marked = false;
        }

        public IRStmt first() {
            return stmts.get(0);
        }

        public IRStmt last() {
            return stmts.get(stmts.size() - 1);
        }
    }

    public List<BasicBlock> blocks;

    /**
     * Construct all the basicblocks from a list of statements
     * 
     * @param stmts
     */
    public BasicBlocks(List<IRStmt> stmts) {
        blocks = new ArrayList<BasicBlock>();
        int count = 0;
        boolean first = true;
        List<IRStmt> block = null;
        String blockId = NumberGetter.uniqueNumber();

        // slicing out basicblocks, starting with label and end with jump/cjump
        for (IRStmt stmt : stmts) {
            if (first) {
                block = new ArrayList<IRStmt>();
                if (stmt instanceof IRLabel) {
                    block.add(stmt);
                } else {
                    block.add(new IRLabel("_basicblocks_" + blockId + "_" + Integer.toString(count)));
                    block.add(stmt);
                }
                first = false;
            } else if (stmt instanceof IRJump || stmt instanceof IRCJump) {
                first = true;
                block.add(stmt);
                BasicBlock blk = new BasicBlock(block);
                blocks.add(blk);
                block = null;
            } else {
                block.add(stmt);
            }
            count++;
        }
        if (block != null) {
            blocks.add(new BasicBlock(block));
        }

        // for each basicblocks, find out the successsors
        for (BasicBlock b : blocks) {
            b.successors = new ArrayList<BasicBlock>();
            for (BasicBlock bs : blocks) {
                if (b.last() instanceof IRJump) {
                    if (((IRName) ((IRJump) b.last()).target()).name() == ((IRLabel) bs.first()).name()) {
                        b.successors.add(bs);
                    }
                } else if (b.last() instanceof IRCJump) {
                    if (((IRCJump) b.last()).trueLabel() == ((IRLabel) bs.first()).name()) {
                        b.successors.add(bs);
                    }
                    if (((IRCJump) b.last()).falseLabel() == ((IRLabel) bs.first()).name()) {
                        b.successors.add(bs);
                    }
                }
            }
        }
    }

    /**
     * generate trace (list of statements that are reordered) from the constucted
     * BasicBlocks
     * 
     * @return
     */
    public List<IRStmt> GenTrace() {
        List<BasicBlock> Q = blocks;
        List<IRStmt> ret = new ArrayList<IRStmt>();
        while (!Q.isEmpty()) {
            List<BasicBlock> T = new ArrayList<BasicBlock>();
            BasicBlock b = Q.get(0);
            Q.remove(0);
            while (!b.isMarked()) {
                b.mark();
                T.add(b);
                for (BasicBlock c : b.successors) {
                    if (!c.isMarked()) {
                        b = c;
                        break;
                    }
                }
            }
            // add all elements in T to ret
            for (BasicBlock blk : T) {
                ret.addAll(blk.stmts);
            }
        }
        return ret;
    }
}