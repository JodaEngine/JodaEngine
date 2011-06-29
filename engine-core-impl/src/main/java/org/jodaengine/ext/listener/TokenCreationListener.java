package org.jodaengine.ext.listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jodaengine.process.token.Token;

/**
 * An extension listener, which will be informed whenever a {@link Token} is created.
 * 
 * @author Jan Rehwaldt
 * @since 2011-06-29
 */
public interface TokenCreationListener {
    
    /**
     * This method indicates that a new token is created
     * and is called even before the token is in INIT state.
     * 
     * @param token the token, which is created
     * @param parentToken the parental token, which triggered the creation
     */
    void tokenCreatedPreInit(@Nonnull Token token,
                             @Nullable Token parentToken);
}
