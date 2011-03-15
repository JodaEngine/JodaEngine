package de.hpi.oryxengine.routing.behaviour;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.hpi.oryxengine.factory.node.RoutingBehaviourTestFactory;
import de.hpi.oryxengine.process.structure.Node;
import de.hpi.oryxengine.process.token.Token;
import de.hpi.oryxengine.process.token.TokenImpl;

/**
 * The Class BPMNUnstructuredJoinSplitTest. We want to test that unstructured AND splits and joins are handled
 * correctly.
 */
public class BPMNUnstructuredJoinSplitTest {

    private Token initialToken;
    private Node node1, node2, node3, innerJoinNode, outerJoinNode, endNode;

    /**
     * Before test.
     */
    @BeforeClass
    public void beforeTest() {

        initialToken = initializeToken();
    }

    /**
     * Test overall behaviour. Start with the execution of the first node, validate results on the way and the final
     * result.
     */
    @Test
    public void testOverallBehaviour() {

        List<Token> newTokens;
        try {
            newTokens = initialToken.executeStep();
            assertEquals(newTokens.size(), 3,
            "Two new tokens have been created, one one the inner join node and one on node3");

            // we need to find out, which of the new tokens is on node3 and which on the inner join node
            Token tokenOnNode1 = tokenOnNode(newTokens, node1);
            Token tokenOnNode2 = tokenOnNode(newTokens, node2);
            Token tokenOnNode3 = tokenOnNode(newTokens, node3);
    
            // Execute node 1 and 2.
            tokenOnNode1.executeStep();
            tokenOnNode2.executeStep();
            
            // Execute join node, actually tokenOnNode1 and tokenOnNode2 have advanced to the join Node
            newTokens = tokenOnNode1.executeStep();
            assertEquals(newTokens.size(), 0,
                "Only the first token has signaled, that it has arrived, no tokens should have been synchronized");
            newTokens = tokenOnNode2.executeStep();
            assertEquals(newTokens.size(), 1, "The two tokens should be synchronized now");

            Token innerJoinedToken = newTokens.get(0);
            assertEquals(innerJoinedToken.getCurrentNode(), outerJoinNode, "It should be on the outer join now.");

            // Execute node 3
            tokenOnNode3.executeStep();

            // Execute outer join
            tokenOnNode3.executeStep();
            
            newTokens = innerJoinedToken.executeStep();
            assertEquals(newTokens.size(), 1, "Again, there should be one synchronized token");
            assertEquals(newTokens.get(0).getCurrentNode(), endNode,
                "It should point to the node after the outer join, which is the endNode");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Initialize tokens and graph structure.
     * 
     *  ->N1->->->
     * S->N2->IJ->OJ->E
     *  ->N3->
     * @return the list
     */
    private Token initializeToken() {

        Node splitNode = new RoutingBehaviourTestFactory().createWithAndSplit();
        node1 = new RoutingBehaviourTestFactory().createWithAndSplit();
        node2 = new RoutingBehaviourTestFactory().createWithAndSplit();
        node3 = new RoutingBehaviourTestFactory().createWithAndSplit();
        innerJoinNode = new RoutingBehaviourTestFactory().createWithAndJoin();
        outerJoinNode = new RoutingBehaviourTestFactory().createWithAndJoin();
        endNode = new RoutingBehaviourTestFactory().createWithAndSplit();

        splitNode.transitionTo(node1);
        splitNode.transitionTo(node2);
        splitNode.transitionTo(node3);
        node1.transitionTo(innerJoinNode);
        node2.transitionTo(innerJoinNode);
        innerJoinNode.transitionTo(outerJoinNode);
        node3.transitionTo(outerJoinNode);
        outerJoinNode.transitionTo(endNode);

        Token token = new TokenImpl(splitNode);

        return token;
    }

    /**
     * Returns the token out of the candidate nodes that points to the given node.
     * 
     * @param candidateTokens
     *            the candidate tokens
     * @param n
     *            the node that the pointing token is searched for
     * @return the token
     */
    private Token tokenOnNode(List<Token> candidateTokens, Node n) {

        Token result = null;
        for (Token token : candidateTokens) {
            if (token.getCurrentNode() == n) {
                result = token;
                break;
            }
        }
        return result;
    }
}