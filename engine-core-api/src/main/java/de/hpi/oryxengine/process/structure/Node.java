package de.hpi.oryxengine.process.structure;

import java.util.List;

import de.hpi.oryxengine.activity.Activity;
import de.hpi.oryxengine.process.instance.ProcessInstance;
import de.hpi.oryxengine.routing.behaviour.join.JoinBehaviour;
import de.hpi.oryxengine.routing.behaviour.split.SplitBehaviour;

/**
 * The Interface for Nodes. Nodes are hubs in the graph representation of a process.
 */
public interface Node {

    /**
     * Gets the activity. The activity is the behavior of a node.
     * 
     * @return the activity
     */
    Activity getActivity();

    /**
     * Sets the activity. The activity is the behavior of a node.
     * 
     * @param activity
     *            the new activity
     */
    void setActivity(Activity activity);

    /**
     * Sets the outgoing behaviour.
     *
     * @param outgoingBehaviour the new outgoing behaviour
     */
    void setOutgoingBehaviour(SplitBehaviour outgoingBehaviour);
    
    /**
     * Sets the incoming behaviour.
     *
     * @param incomingBehaviour the new incoming behaviour
     */
    void setIncomingBehaviour(JoinBehaviour incomingBehaviour);
    
    /**
     * Gets the incoming behaviour.
     *
     * @return the incoming behaviour
     */
    JoinBehaviour getIncomingBehaviour();
    
    /**
     * Gets the outgoing behaviour.
     *
     * @return the outgoing behaviour
     */
    SplitBehaviour getOutgoingBehaviour();

    /**
     * Next.
     * 
     * @return the next Node(s) depending on the node (normal nodes vs. Splits which have multiple next nodes).
     */
    List<Transition> getTransitions();

    /**
     * Describes a new outgoing edge to the given node.
     * 
     * @param node
     *            the node to which a new transition shall be established
     */
    void transitionTo(Node node);

    /**
     * Transition to with condition.
     *
     * @param node the destination
     * @param c the condition
     */
    void transitionToWithCondition(Node node, Condition c);
    
    /**
     * Gets the id of the node.
     * 
     * @return the id
     */
    String getId();

    /**
     * Sets the id of the node.
     * 
     * @param id
     *            the new id
     */
    void setId(String id);

    /**
     * Execute some sort of behaviour.
     *
     * @param instance The process instance to execute
     * @return the list
     */
    List<ProcessInstance> execute(ProcessInstance instance);

}
