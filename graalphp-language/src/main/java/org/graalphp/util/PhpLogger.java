package org.graalphp.util;

import java.util.Date;

/**
 * @author abertschi
 *
 * Simple logging interface to use in graalphp.
 * As for now, we dont use java.logging classes to avoid issues with graal native.
 */
public class PhpLogger implements Logger {

    final private String name;

    private PhpLogger(String name) {
        this.name = name;
    }

    public static Logger getLogger(String name) {
        // XXX: to reduce object creation make logger static
        // fine for now, Logger is debug utility
        return new PhpLogger(name);
    }

    @Override
    public void fine(String msg) {
        System.err.println(format(msg, "fine"));
    }

    @Override
    public void info(String msg) {
        System.err.println(format(msg, "info"));
    }

    @Override
    public void finest(String msg) {
        System.err.println(format(msg, "finest"));
    }

    @Override
    public void parserEnumerationError(String msg) {
        System.err.println(format(msg, "ERROR"));
    }

    private String format(String msg, String lvl) {
//        return String.format("[%1$tF %1$tT] [%2$-7s] %3$s",
//                new Date(),
//                lvl,
//                msg
//        );
        return String.format("[%1$-15s] [%2$-7s] %3$s",
                name,
                lvl,
                msg
        );
    }
}

