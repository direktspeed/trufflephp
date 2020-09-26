package org.trufflephp;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.source.Source;
import org.trufflephp.nodes.PhpGlobalRootNode;
import org.trufflephp.parser.PhpParser;
import org.trufflephp.parser.StmtVisitor;
import org.trufflephp.types.PhpNull;
import org.trufflephp.util.PhpLogger;
import org.trufflephp.util.Logger;

@TruffleLanguage.Registration(
        id = PhpLanguage.ID,
        name = "trufflephp",
        defaultMimeType = PhpLanguage.MIME_TYPE,
        characterMimeTypes = PhpLanguage.MIME_TYPE,
        contextPolicy = ContextPolicy.SHARED,
        fileTypeDetectors = PhpFileDetector.class)

public final class PhpLanguage extends TruffleLanguage<PhpContext> {

    public static final String ID = "php";
    public static final String MIME_TYPE = "application/x-php";

    public static boolean RETURN_LAST_EXPR = true;

    private final static Logger LOG = PhpLogger
            .getLogger(PhpLanguage.class.getCanonicalName());

    public PhpLanguage() {
    }

    @Override
    protected PhpContext createContext(Env env) {
        return new PhpContext(this, env);
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        Source source = request.getSource();
        PhpParser phpParser = new PhpParser(this);

        StmtVisitor.StmtVisitorContext parseResult = phpParser.parseSource(source);
        PhpGlobalRootNode evalMain =
                new PhpGlobalRootNode(
                        this,
                        parseResult.getScope().getFrameDesc(),
                        parseResult.getStmts(),
                        RETURN_LAST_EXPR);

        getCurrentContext().setRootScope(parseResult.getScope());
        return Truffle.getRuntime().createCallTarget(evalMain);
    }

    @Override
    protected boolean isVisible(PhpContext context, Object value) {
        return !InteropLibrary.getFactory().getUncached(value).isNull(value);
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        if (!(object instanceof TruffleObject)) {
            return false;
        } else if (object instanceof PhpNull) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String toString(PhpContext context, Object value) {
        return toString(value);
    }

    public static String toString(Object value) {
        try {
            if (value == null) {
                return "ANY";
            }
            InteropLibrary interop = InteropLibrary.getFactory().getUncached(value);
            if (interop.fitsInLong(value)) {
                return Long.toString(interop.asLong(value));
            } else if (interop.fitsInDouble(value)) {
                return Double.toString(interop.asDouble(value));
            } else if (interop.isBoolean(value)) {
                return Boolean.toString(interop.asBoolean(value));
            } else if (interop.isString(value)) {
                return interop.asString(value);
            } else if (interop.isNull(value)) {
                return "NULL";
            } else if (interop.isExecutable(value)) {
                return "Function";
            } else if (interop.hasMembers(value)) {
                return "Object";
            } else {
                return "Datatype unsupported by trufflephp";
            }
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreter();
            throw new AssertionError();
        }
    }

    @Override
    protected Object findMetaObject(PhpContext context, Object value) {
        return getMetaObject(value);
    }

    public static String getMetaObject(Object value) {
        if (value == null) {
            return "ANY";
        }
        InteropLibrary interop = InteropLibrary.getFactory().getUncached(value);
        if (interop.isNumber(value)) {
            return "Number";
        } else if (interop.isBoolean(value)) {
            return "Boolean";
        } else if (interop.isString(value)) {
            return "String";
        } else if (interop.isNull(value)) {
            return "NULL";
        } else if (interop.isExecutable(value)) {
            return "Function";
        } else if (interop.hasMembers(value)) {
            return "Object";
        } else {
            return "Unsupported";
        }
    }

    public static PhpContext getCurrentContext() {
        return getCurrentContext(PhpLanguage.class);
    }
}

