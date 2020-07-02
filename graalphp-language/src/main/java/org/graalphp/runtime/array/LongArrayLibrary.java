package org.graalphp.runtime.array;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import java.util.Arrays;

/**
 * @author abertschi
 */
@ExportLibrary(value = ArrayLibrary.class, receiverType = long[].class)
@GenerateUncached
public class LongArrayLibrary {

    @ExportMessage
    protected static boolean isArray(long[] store) {
        return true;
    }

    @ExportMessage
    protected static boolean acceptsValue(long[] receiver, Object value) {
        return value instanceof Long;
    }

    @ExportMessage
    protected static long read(long[] store, int index) {
        return store[index];
    }

    @ExportMessage
    static class Write {
        @Specialization
        protected static void write(long[] store, int index, long value) {
            store[index] = value;
        }
    }

    @ExportMessage
    protected static LongArrayAllocator allocator(long[] receiver) {
        return LongArrayAllocator.ALLOCATOR;
    }

    @ExportMessage
    @TruffleBoundary
    protected static String arrayToString(long[] receiver) {
        return Arrays.toString(receiver);
    }

    @ExportMessage
    static class GeneralizeForValue {
        @Specialization
        protected static ArrayAllocator generalizeForValue(long[] receiver, long newValue) {
            return LongArrayAllocator.ALLOCATOR;
        }

        @Specialization
        protected static ArrayAllocator generalizeForValue(long[] receiver, Object newValue) {
            return ObjectArrayAllocator.ALLOCATOR;
        }
    }
}
