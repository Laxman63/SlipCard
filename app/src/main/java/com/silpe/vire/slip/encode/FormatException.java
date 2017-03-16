package com.silpe.vire.slip.encode;

public final class FormatException extends ReaderException {

    private static final FormatException INSTANCE = new FormatException();

    static {
        INSTANCE.setStackTrace(NO_TRACE); // since it's meaningless
    }

    private FormatException() {
    }

    private FormatException(Throwable cause) {
        super(cause);
    }

    public static FormatException getFormatInstance() {
        return isStackTrace ? new FormatException() : INSTANCE;
    }

    public static FormatException getFormatInstance(Throwable cause) {
        return isStackTrace ? new FormatException(cause) : INSTANCE;
    }
}

abstract class ReaderException extends Exception {

    // disable stack traces when not running inside test units
    protected static final boolean isStackTrace =
            System.getProperty("surefire.test.class.path") != null;
    protected static final StackTraceElement[] NO_TRACE = new StackTraceElement[0];

    ReaderException() {
        // do nothing
    }

    ReaderException(Throwable cause) {
        super(cause);
    }

    // Prevent stack traces from being taken
    @Override
    public final synchronized Throwable fillInStackTrace() {
        return null;
    }

}