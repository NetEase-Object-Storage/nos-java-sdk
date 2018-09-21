package com.netease.cloud.exception;

import com.netease.cloud.ClientException;

/**
 * SDK operation aborted exception.
 */
public class AbortedException extends ClientException {
    private static final long serialVersionUID = 1L;

    public AbortedException(String message, Throwable t) {
        super(message, t);
    }

    public AbortedException(Throwable t) {
        super("", t);
    }

    public AbortedException(String message) {
        super(message);
    }

    public AbortedException() {
        super("");
    }

    /**
     * {@inheritDoc}
     * An aborted exception is not intended to be retried.
     */
    @Override
    public boolean isRetryable() {
        return false;
    }
}
