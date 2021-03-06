package org.jodaengine.ext.listener.token;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.jodaengine.exception.JodaEngineException;
import org.jodaengine.ext.listener.AbstractTokenListener;
import org.jodaengine.navigator.Navigator;
import org.jodaengine.node.activity.custom.AutomatedDummyActivity;
import org.jodaengine.node.incomingbehaviour.SimpleJoinBehaviour;
import org.jodaengine.node.outgoingbehaviour.TakeAllSplitBehaviour;
import org.jodaengine.process.instance.AbstractProcessInstance;
import org.jodaengine.process.instance.ProcessInstance;
import org.jodaengine.process.structure.Node;
import org.jodaengine.process.structure.NodeImpl;
import org.jodaengine.process.token.AbstractToken;
import org.jodaengine.process.token.BpmnToken;
import org.jodaengine.process.token.TokenBuilder;
import org.jodaengine.process.token.builder.BpmnTokenBuilder;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * The Class TokenPluginTest.
 */
public class TokenPluginTest {
    private AbstractToken token = null;
    private ArgumentCaptor<ActivityLifecycleChangeEvent> eventCapturer = null;
    private AbstractTokenListener mock = null;
    private TokenBuilder builder = null;
    private Node node1;

    /**
     * Sets up a token that points to a node and registers a mocked plugin.
     */
    @BeforeMethod
    public void setUp() {

        String dummyString = "s.out";

        node1 = new NodeImpl(new AutomatedDummyActivity(dummyString), new SimpleJoinBehaviour(),
            new TakeAllSplitBehaviour());
        builder = new BpmnTokenBuilder(mock(Navigator.class), null);
        AbstractProcessInstance processInstance = new ProcessInstance(null, builder);
        this.token = new BpmnToken(node1, processInstance, null, null);

        mock = mock(AbstractTokenListener.class);
        token.registerListener(mock);
        this.eventCapturer = ArgumentCaptor.forClass(ActivityLifecycleChangeEvent.class);
    }

    /**
     * Test the deregistration of the plugin.
     * 
     * @throws JodaEngineException the JodaEngine exception
     */
    @Test
    public void testDeregistration()
    throws JodaEngineException {

        token.deregisterListener(mock);
        token.executeStep();
        verify(mock, never()).update(eq(this.token), this.eventCapturer.capture());
    }

//    /**
//     * Tests that new tokens that are created by this token receive the same plugins the creator has.
//     * 
//     * @throws JodaEngineException the JodaEngine exception
//     */
//    @Test
//    public void testPluginRegistrationInheritance()
//    throws JodaEngineException {
//
//        AbstractToken newToken = (AbstractToken) token.createToken(node1);
//        newToken.executeStep();
//        verify(mock, times(3)).update(eq(newToken), this.eventCapturer.capture());
//    }

    /**
     * Tests that newly created tokens do not receive plugins that were deregistered before.
     * 
     * @throws JodaEngineException the JodaEngine exception
     */
    @Test
    public void testPluginDeregistrationInheritance()
    throws JodaEngineException {
        
        token.deregisterListener(mock);
        AbstractToken newToken = (AbstractToken) token.createToken(token.getCurrentNode(), null);
        newToken.executeStep();
        verify(mock, never()).update(eq(newToken), this.eventCapturer.capture());
    }
}
