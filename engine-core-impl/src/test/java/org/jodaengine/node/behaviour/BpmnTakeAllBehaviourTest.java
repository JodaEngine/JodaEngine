package org.jodaengine.node.behaviour;

import static org.testng.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.jodaengine.node.activity.NullActivity;
import org.jodaengine.node.incomingbehaviour.IncomingBehaviour;
import org.jodaengine.node.incomingbehaviour.SimpleJoinBehaviour;
import org.jodaengine.node.outgoingbehaviour.OutgoingBehaviour;
import org.jodaengine.node.outgoingbehaviour.TakeAllSplitBehaviour;
import org.jodaengine.process.definition.bpmn.BpmnProcessDefinitionBuilder;
import org.jodaengine.process.instance.ProcessInstance;
import org.jodaengine.process.structure.ControlFlow;
import org.jodaengine.process.structure.Node;
import org.jodaengine.process.token.BpmnToken;
import org.jodaengine.process.token.Token;
import org.jodaengine.process.token.builder.BpmnTokenBuilder;
import org.mockito.Mockito;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * The test of the TakeAllBehaviour-activity.
 */
public class BpmnTakeAllBehaviourTest {

    /** The process token. */
    private Token token = null;

    /**
     * Set up. A token is built.
     */
    @BeforeClass
    public void setUp() {

        token = simpleToken();
    }

    /**
     * Test class.
     * 
     */
    @Test
    public void testClass() {

        Node node = token.getCurrentNode();
        Node nextNode = node.getOutgoingControlFlows().get(0).getDestination();

        try {
            executeSplitAndJoin(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(token.getCurrentNode(), nextNode);
    }

    /**
     * Tear down.
     */
    @AfterClass
    public void tearDown() {

    }

    /**
     * Simple token. An activity is set up, it gets a behavior and a {@link ControlFlow} to a second node.
     * 
     * @return the process instance that was created within the method
     */
    private Token simpleToken() {

        BpmnProcessDefinitionBuilder builder = BpmnProcessDefinitionBuilder.newBuilder();
        
        Node node = builder.getNodeBuilder().setActivityBehavior(new NullActivity())
        .setIncomingBehaviour(new SimpleJoinBehaviour()).setOutgoingBehaviour(new TakeAllSplitBehaviour()).buildNode();

        Node node2 = builder.getNodeBuilder().setActivityBehavior(new NullActivity())
        .setIncomingBehaviour(new SimpleJoinBehaviour()).setOutgoingBehaviour(new TakeAllSplitBehaviour()).buildNode();

        builder.getControlFlowBuilder().controlFlowGoesFromTo(node, node2).buildControlFlow();

        return new BpmnToken(node, new ProcessInstance(null, Mockito.mock(BpmnTokenBuilder.class)), null);
    }

    /**
     * Execute split and join.
     * 
     * @param token
     *            the token
     * @return the list
     * @throws Exception
     *             the exception
     */
    private Collection<Token> executeSplitAndJoin(Token token)
    throws Exception {

        Node node = token.getCurrentNode();
        IncomingBehaviour incomingBehaviour = node.getIncomingBehaviour();
        OutgoingBehaviour outgoingBehaviour = node.getOutgoingBehaviour();

        Collection<Token> joinedTokens = incomingBehaviour.join(token);

        return outgoingBehaviour.split(joinedTokens);
    }

}
