package org.jodaengine.ext.debugging.listener;

import org.jodaengine.ext.Extension;
import org.jodaengine.ext.debugging.api.DebuggerCommand;
import org.jodaengine.ext.debugging.api.DebuggerService;
import org.jodaengine.ext.debugging.shared.DebuggerInstanceAttribute;
import org.jodaengine.ext.listener.TokenCreationListener;
import org.jodaengine.process.token.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation creates a {@link DebuggerInstanceAttribute} within a {@link Token}.
 * It may a new one be created or the parental one referenced.
 * 
 * @author Jan Rehwaldt
 * @since 2011-06-29
 */
@Extension(DebuggerService.DEBUGGER_SERVICE_NAME)
public class DebuggerTokenCreationListener implements TokenCreationListener {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Override
    public void tokenCreatedPreInit(Token token,
                                    Token parentToken) {
        
        logger.debug("Token {} created [parent: {}]", token, parentToken);
        
        if (parentToken != null) {
            //
            // reference the parental attribute
            //
            DebuggerInstanceAttribute parentAttribute = DebuggerInstanceAttribute.getAttribute(parentToken);
            DebuggerInstanceAttribute.setAttribute(parentAttribute, token);
            
            //
            // register the parental command for the new token, command may be null anyway
            //
            DebuggerCommand parentCommand = parentAttribute.getCommand(parentToken);
            parentAttribute.setCommand(token, parentCommand);
            
        } else {
            //
            // create a new attribute
            //
            DebuggerInstanceAttribute.getAttribute(token);
        }
    }
    
}
