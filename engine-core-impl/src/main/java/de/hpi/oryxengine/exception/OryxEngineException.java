package de.hpi.oryxengine.exception;

/**
 * Runtime exception that is the superclass of all OryxEngine exceptions.
 * 
 * @author Gerardo Navarro Suarez
 */
public class OryxEngineException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new oryx engine exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public OryxEngineException(String message, Throwable cause) {

        super(message, cause);
    }

    /**
     * Instantiates a new oryx engine exception.
     *
     * @param message the message
     */
    public OryxEngineException(String message) {

        super(message);
    }
}