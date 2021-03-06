package org.trufflephp.nodes;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.trufflephp.types.PhpTypes;

/**
 * Representation of a statement.
 * A statement does not return a value.
 * Parent node of all PHP specific nodes.
 *
 * @author abertschi
 */
@NodeInfo(description = "Abstract base node for all PHP nodes")
@TypeSystemReference(PhpTypes.class)
public abstract class PhpStmtNode extends PhpNode {

    private static final int SRC_SECTION_NOT_SET = -1;

    // TODO: implement get source section
    private int srcSectionStart = SRC_SECTION_NOT_SET;
    private int srcSectionLen = SRC_SECTION_NOT_SET;

    /**
     * Stmts evaluate to nothing
     **/
    public abstract void executeVoid(VirtualFrame frame);

    public final void setSourceSection(int charLeft, int len) {
        assert (srcSectionStart == SRC_SECTION_NOT_SET) : "source section already set";

        if (charLeft < 0) {
            throw new IllegalArgumentException("charleft < 0");
        } else if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }
        this.srcSectionStart = charLeft;
        this.srcSectionLen = len;
    }

    public final boolean hasSourceSection() {
        return srcSectionStart != SRC_SECTION_NOT_SET;
    }
}
