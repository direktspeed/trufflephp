package org.trufflephp.parser;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import org.trufflephp.FunctionRegistry;
import org.trufflephp.types.PhpFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * Pojo to represent scoping in PHP
 *
 * @author abertschi
 */
public class ParseScope {

    // XXX: This does not yet model nested functions

    // XXX: ref to self if this is global
    private ParseScope global;

    private FrameDescriptor frameDesc;
    private Map<String, FrameSlot> vars;
    private FunctionRegistry functions;

    private ParseScope(FrameDescriptor frameDesc) {
        this.frameDesc = frameDesc;
        this.vars = new HashMap<>();
        this.functions = new FunctionRegistry();
    }

    public static ParseScope newGlobalScope() {
        FrameDescriptor frameDesc = new FrameDescriptor();
        ParseScope scope = new ParseScope(frameDesc);
        scope.setGlobal(scope);
        return scope;
    }

    public ParseScope(FrameDescriptor frameDesc, ParseScope global) {
        this.frameDesc = frameDesc;
        this.vars = new HashMap<>();
        this.global = global;
        this.functions = new FunctionRegistry();
    }

    // XXX: Lookup function in current and global scope
    @TruffleBoundary
    public PhpFunction resolveFunction(String name) {
        PhpFunction fn = this.functions.getFunction(name);
        if (fn == null && !isGlobalScope()) {
            return this.global.resolveFunction(name);
        }
        return fn;
    }

    // XXX: Lookup variable slot in current and global scope
    public FrameSlot resolveVariable(String name) {
        FrameSlot slot = this.vars.get(name);
        if (slot == null && !isGlobalScope()) {
            return this.global.vars.get(name);
        }
        return slot;
    }

    // XXX: Lookup variable slot in current and global scope
    // XXX: we dont use recursion such that inlining is possible
    @TruffleBoundary
    public FrameSlot resolveAndRemoveVariable(String name) {
        FrameSlot slot = resolveAndRemoveVariable(this, name);
        if (slot == null && !isGlobalScope()) {
            slot = resolveAndRemoveVariable(this.global, name);
        }
        return slot;
    }

    private FrameSlot resolveAndRemoveVariable(ParseScope scope, String name) {
        FrameSlot slot = scope.vars.get(name);
        if (slot != null) {
            scope.vars.remove(name);
        }
        return slot;
    }

    public FunctionRegistry getFunctions() {
        return this.functions;
    }

    public ParseScope getGlobal() {
        return global;
    }

    public void setGlobal(ParseScope global) {
        this.global = global;
    }

    public FrameDescriptor getFrameDesc() {
        return frameDesc;
    }

    public void setFrameDesc(FrameDescriptor frameDesc) {
        this.frameDesc = frameDesc;
    }

    public Map<String, FrameSlot> getVars() {
        return vars;
    }

    public void setVars(Map<String, FrameSlot> vars) {
        this.vars = vars;
    }

    public boolean isGlobalScope() {
        assert global != null : "global can never be null";
        return this.global == this;
    }
}
