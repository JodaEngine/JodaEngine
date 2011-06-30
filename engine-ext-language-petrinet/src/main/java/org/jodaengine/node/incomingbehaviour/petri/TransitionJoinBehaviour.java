package org.jodaengine.node.incomingbehaviour.petri;

import java.util.ArrayList;
import java.util.List;

import org.jodaengine.node.incomingbehaviour.IncomingBehaviour;
import org.jodaengine.process.instance.AbstractProcessInstance;
import org.jodaengine.process.structure.Node;
import org.jodaengine.process.structure.ControlFlow;
import org.jodaengine.process.token.Token;
import org.jodaengine.process.token.TokenUtil;


/**
 * The Class TransitionJoinBehaviour. Consumes tokens and checks if it's possible to join.
 */
public class TransitionJoinBehaviour implements IncomingBehaviour {

    @Override
    // The join is not directly needed, but the call of the method consumeTokens is important.
    public List<Token> join(Token token) {
        consumeTokens(token);
        List<Token> tokens = new ArrayList<Token>();
        tokens.add(token);
        return tokens;
    }

    @Override
    // In the case of petri nets, it is also called isEnabled
    public boolean joinable(Token token, Node node) {
        TokenUtil util = new TokenUtil();
        
        // Attention: the node is not the current node of the token, it is a reachable Transition from the current Place of the token.
        // The behaviour itself does not know the node which it is belonging to.
        
        // Now check each place before the petri transition if there are enough tokens.
        for (ControlFlow t : node.getIncomingControlFlows()) {
            if (util.getTokensWhichAreOnNode(t.getSource(), token.getInstance()).size() == 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Consumes used tokens. 
     *
     * @param controlFlows the incoming {@link ControlFlow}s to get the source places
     * @param instance the instance
     */
    private void consumeTokens(Token token) {
        List<ControlFlow> controlFlows = token.getCurrentNode().getIncomingControlFlows();
        AbstractProcessInstance instance = token.getInstance();
        TokenUtil util = new TokenUtil();
        List<Token> oldTokensOnPlace;
        
        for (ControlFlow t : controlFlows) {
            Node placeBeforePetriTransition = t.getSource();
            // Get Token, which are still there
            oldTokensOnPlace = util.getTokensWhichAreOnNode(placeBeforePetriTransition, instance);
            
            // One token of the place should be deleted.
            // Because these are ordinary petri net's all tokens are equal and therefore we can delete just the first.
            if (oldTokensOnPlace.size() > 0) {
                //oldTokens.get(0).setCurrentNode(nextPetriTranisiton);
                Token oldToken = oldTokensOnPlace.get(0);
                instance.removeToken(oldToken);
                // Remove the consumed token from the navigator.
                token.getNavigator().removeTokenFromScheduler(oldToken);
            }
            
            
        }
    }

}
