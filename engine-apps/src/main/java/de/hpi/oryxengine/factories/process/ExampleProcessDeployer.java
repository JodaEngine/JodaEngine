package de.hpi.oryxengine.factories.process;

import de.hpi.oryxengine.activity.impl.AddNumbersAndStoreActivity;
import de.hpi.oryxengine.activity.impl.EndActivity;
import de.hpi.oryxengine.activity.impl.NullActivity;
import de.hpi.oryxengine.process.definition.NodeParameter;
import de.hpi.oryxengine.process.definition.NodeParameterImpl;
import de.hpi.oryxengine.process.definition.ProcessBuilderImpl;
import de.hpi.oryxengine.process.structure.ActivityBlueprint;
import de.hpi.oryxengine.process.structure.ActivityBlueprintImpl;
import de.hpi.oryxengine.process.structure.Node;
import de.hpi.oryxengine.routing.behaviour.incoming.impl.SimpleJoinBehaviour;
import de.hpi.oryxengine.routing.behaviour.outgoing.impl.TakeAllSplitBehaviour;


/**
 * A factory for creating ExampleProcessToken objects.
 * These objects just have 2 add Number activities.
 */
public class ExampleProcessDeployer extends AbstractProcessDeployer {
    
    /** The node1. */
    private Node node1;
    
    /** The node2. */
    private Node node2;
    
    /** The start node. */
    private Node startNode;
    
    /**
     * Instantiates a new example process token factory.
     */
    public ExampleProcessDeployer() {
        
        builder = new ProcessBuilderImpl();

    }
    
    /**
     * Initializes the nodes.
     */
    public void initializeNodes() {
        NodeParameter param = new NodeParameterImpl(NullActivity.class, new SimpleJoinBehaviour(),
            new TakeAllSplitBehaviour());
        startNode = builder.createStartNode(param);

        Class<?>[] constructorSig = {String.class, int[].class};
        int[] ints = {1, 1};
        Object[] params = {"result", ints};
        ActivityBlueprint blueprint = new ActivityBlueprintImpl(AddNumbersAndStoreActivity.class, constructorSig, params);
        param.setActivityBlueprint(blueprint);
        node1 = builder.createNode(param);
        node2 = builder.createNode(param);
        builder.createTransition(startNode, node1).createTransition(node1, node2);
        
        param.setActivityClassOnly(EndActivity.class);
        Node endNode = builder.createNode(param);
        builder.createTransition(node2, endNode);
    }

}