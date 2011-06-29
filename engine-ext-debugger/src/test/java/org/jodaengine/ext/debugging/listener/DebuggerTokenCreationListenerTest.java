package org.jodaengine.ext.debugging.listener;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jodaengine.ext.debugging.api.DebuggerCommand;
import org.jodaengine.ext.debugging.shared.DebuggerInstanceAttribute;
import org.jodaengine.ext.debugging.util.DebuggerInstanceAttributeKeyProvider;
import org.jodaengine.ext.listener.TokenCreationListener;
import org.jodaengine.process.token.Token;
import org.jodaengine.util.testing.AbstractJodaEngineTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * This class tests the proper function of {@link DebuggerTokenCreationListener}.
 * 
 * @author Jan Rehwaldt
 * @since 2011-06-29
 */
public class DebuggerTokenCreationListenerTest extends AbstractJodaEngineTest {
    
    private Token mockToken = null;
    private Token mockParentToken = null;
    
    private DebuggerInstanceAttribute mockAttribute = null;
    
    /**
     * Setup.
     */
    @BeforeMethod
    public void setUp() {
        this.mockAttribute = mock(DebuggerInstanceAttribute.class);
        this.mockToken = mock(Token.class);
        this.mockParentToken = mock(Token.class);
        
        when(this.mockParentToken.getAttribute(
            DebuggerInstanceAttributeKeyProvider.getAttributeKey())).thenReturn(this.mockAttribute);
        when(this.mockAttribute.getCommand(this.mockParentToken)).thenReturn(DebuggerCommand.STEP_OVER);
    }
    
    /**
     * Tests the create method with a parent token.
     * 
     * It should preserve the {@link DebuggerInstanceAttribute}.
     */
    @Test
    public void testCreationWithParentWillPreserveParentAttribute() {
        
        TokenCreationListener listener = new DebuggerTokenCreationListener();
        listener.tokenCreatedPreInit(this.mockToken, this.mockParentToken);
        
        verify(this.mockToken, times(1)).setAttribute(
            DebuggerInstanceAttributeKeyProvider.getAttributeKey(),
            this.mockAttribute);
    }
    
    /**
     * Tests the create method without a parent token.
     * 
     * It should create a {@link DebuggerInstanceAttribute}.
     */
    @Test
    public void testCreationWithoutParentWillCreateAnAttribute() {
        
        TokenCreationListener listener = new DebuggerTokenCreationListener();
        listener.tokenCreatedPreInit(this.mockToken, null);
        
        verify(this.mockToken, times(1)).setAttribute(
            eq(DebuggerInstanceAttributeKeyProvider.getAttributeKey()),
            any());
    }
    
    /**
     * Tests the create method with a parent token will preserve the {@link DebuggerCommand}.
     */
    @Test
    public void testCreationWithParentWillPreserveParentCommand() {
        
        TokenCreationListener listener = new DebuggerTokenCreationListener();
        listener.tokenCreatedPreInit(this.mockToken, this.mockParentToken);
        
        verify(this.mockAttribute, times(1)).setCommand(
            this.mockToken,
            this.mockAttribute.getCommand(this.mockParentToken));
    }
}
