package de.hpi.oryxengine.process.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hpi.oryxengine.process.structure.Condition;
import de.hpi.oryxengine.process.structure.Node;
import de.hpi.oryxengine.process.structure.NodeImpl;
import de.hpi.oryxengine.process.structure.StartNode;
import de.hpi.oryxengine.process.structure.StartNodeImpl;

/**
 * The Class ProcessBuilderImpl.
 * 
 * @author thorben
 */
public class ProcessBuilderImpl implements ProcessBuilder {

    /** The definition. */
    private ProcessDefinition definition;

    /** The start nodes. */
    private List<StartNode> startNodes = new ArrayList<StartNode>();

    /** The id. */
    private UUID id;

    /** The description. */
    private String description;

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessDefinition buildDefinition() {

        definition = new ProcessDefinitionImpl(id, description, startNodes);
        startNodes = new ArrayList<StartNode>();
        return definition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node createNode(NodeParameter param) {

        Node node = new NodeImpl(param.getActivity(), param.getIncomingBehaviour(), param.getOutgoingBehaviour());
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessBuilder createTransition(Node source, Node destination) {

        source.transitionTo(destination);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessBuilder createTransition(Node source, Node destination, Condition condition) {

        source.transitionToWithCondition(destination, condition);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessBuilder setID(UUID id) {

        this.id = id;
        return this;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessBuilder setDescription(String description) {

        this.description = description;
        return this;

    }

    @Override
    public StartNode createStartNode(StartNodeParameter param) {

        StartNode node = new StartNodeImpl(param.getActivity(), param.getIncomingBehaviour(),
            param.getOutgoingBehaviour(), param.getStartEvent());
        this.startNodes.add(node);
        return node;
    }

}
