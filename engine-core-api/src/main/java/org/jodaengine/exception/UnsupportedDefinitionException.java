package org.jodaengine.exception;

import org.jodaengine.process.definition.ProcessDefinition;

/**
 * An UnsupportedDefinitionException is thrown, if the supplied {@link ProcessDefinition} is correct, but some of its
 * specification is not yet supported by the JodaEngine.
 */
public class UnsupportedDefinitionException extends JodaEngineException {

    /**
     * Creates an exception with an error message.
     * 
     * @param message
     *            the message
     */
    public UnsupportedDefinitionException(String message) {

        super(message);
    }

}
