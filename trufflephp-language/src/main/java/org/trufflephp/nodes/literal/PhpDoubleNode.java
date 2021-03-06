package org.trufflephp.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.trufflephp.nodes.PhpExprNode;

/**
 * Represents floats in trufflephp
 * <p>
 * Must support at least the range and precision of IEEE 754 64-bit double-precision representation.
 * Hence is based on double in java
 *
 * @author abertschi
 */
@NodeInfo(shortName = "double")
public final class PhpDoubleNode extends PhpExprNode {

    private final double val;

    public PhpDoubleNode(double v) {
        this.val = v;
    }

    @Override
    public double executeDouble(VirtualFrame f) {
        return val;
    }

    @Override
    public boolean executeBoolean(VirtualFrame f) {
        return val != 0.0;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return val;
    }

    @Override
    public String toString() {
        return "PhpDoubleNode{" +
                "val=" + val +
                '}';
    }
}
