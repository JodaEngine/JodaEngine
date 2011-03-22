package de.hpi.oryxengine.exception;

/**
 * The Class IllegalNavigationException.
 * Is called if someone invokes an undefined navigation-task on ProcessInstance
 */
public class IllegalNavigationException extends DalmatinaException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant DEFAULT_EXCEPTION_MESSAGE. */
    private static final String DEFAULT_EXCEPTION_MESSAGE = "Please. Don't try to take this way.";

    /**
     * Default Constructor.
     */
    public IllegalNavigationException() {

        super(DEFAULT_EXCEPTION_MESSAGE);
    }
}