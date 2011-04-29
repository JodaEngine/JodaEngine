package de.hpi.oryxengine.example;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.hpi.oryxengine.activity.impl.AddContextNumbersAndStoreActivity;
import de.hpi.oryxengine.activity.impl.BPMNActivityFactory;
import de.hpi.oryxengine.activity.impl.EndActivity;
import de.hpi.oryxengine.activity.impl.NullActivity;
import de.hpi.oryxengine.exception.DalmatinaException;
import de.hpi.oryxengine.navigator.Navigator;
import de.hpi.oryxengine.navigator.NavigatorImplMock;
import de.hpi.oryxengine.process.definition.ProcessDefinitionBuilderImpl;
import de.hpi.oryxengine.process.definition.ProcessDefinition;
import de.hpi.oryxengine.process.definition.ProcessDefinitionBuilder;
import de.hpi.oryxengine.process.definition.ProcessDefinitionImpl;
import de.hpi.oryxengine.process.instance.AbstractProcessInstance;
import de.hpi.oryxengine.process.instance.ProcessInstanceImpl;
import de.hpi.oryxengine.process.structure.ActivityBlueprint;
import de.hpi.oryxengine.process.structure.ActivityBlueprintImpl;
import de.hpi.oryxengine.process.structure.Condition;
import de.hpi.oryxengine.process.structure.Node;
import de.hpi.oryxengine.process.structure.condition.HashMapCondition;
import de.hpi.oryxengine.process.token.Token;
import de.hpi.oryxengine.process.token.TokenImpl;
import de.hpi.oryxengine.routing.behaviour.incoming.impl.SimpleJoinBehaviour;
import de.hpi.oryxengine.routing.behaviour.outgoing.impl.TakeAllSplitBehaviour;
import de.hpi.oryxengine.routing.behaviour.outgoing.impl.XORSplitBehaviour;

/**
 * The Class LoopProcessTest.
 */
public class LoopProcessTest {

    private Token token;
    private Node start;
    private Node end;
    private Node xorSplit;
    private Node xorJoin;
    private Node node;
    private static final int RUNTIMES = 3;

    /**
     * Test the example loopProcess. It should execute several RUNTIMES and then end.
     * 
     * @throws DalmatinaException
     *             the dalmatina exception
     */
    @Test
    public void testLoop()
    throws DalmatinaException {

        assertEquals(token.getCurrentNode(), start);
        token.executeStep();
        assertEquals(token.getCurrentNode(), xorJoin);
        token.executeStep();
        assertEquals(token.getCurrentNode(), node);
        token.executeStep();
        assertEquals(token.getInstance().getContext().getVariable("counter"), 1);
        assertEquals(token.getCurrentNode(), xorSplit);
        token.executeStep();
        assertEquals(token.getCurrentNode(), xorJoin);
        token.executeStep();
        assertEquals(token.getCurrentNode(), node);
        token.executeStep();
        assertEquals(token.getInstance().getContext().getVariable("counter"), 2);
        assertEquals(token.getCurrentNode(), xorSplit);
        token.executeStep();
        assertEquals(token.getCurrentNode(), xorJoin);
        token.executeStep();
        assertEquals(token.getCurrentNode(), node);
        token.executeStep();
        assertEquals(token.getInstance().getContext().getVariable("counter"), 2 + 1);
        assertEquals(token.getCurrentNode(), xorSplit);
        token.executeStep();
        assertEquals(token.getCurrentNode(), end);

    }

    /**
     * Sets up an example process.
     */
    @BeforeClass
    public void setUp() {

        ProcessDefinitionBuilder builder = new ProcessDefinitionBuilderImpl();

        // Create StartNode
        ActivityBlueprint blueprint = new ActivityBlueprintImpl(NullActivity.class);

        start = BPMNActivityFactory.createBPMNNullNode(builder);


        // Create the XORJoin
        xorJoin = BPMNActivityFactory.createBPMNXorGatewayNode(builder);

        // Create a following Node
        node = BPMNActivityFactory.createBPMNAddContextNumbersAndStoreNode(builder, "counter", new String[] {
            "increment", "counter" });

        // Create the XORSplit
        xorSplit = BPMNActivityFactory.createBPMNXorGatewayNode(builder);

        // Create the end
        end = BPMNActivityFactory.createBPMNEndEventNode(builder);

        // Setup XOR DATA
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("counter", RUNTIMES);
        Condition condition1 = new HashMapCondition(map, "<=");
        Condition condition2 = new HashMapCondition(map, "==");

        // Create Transitions
        BPMNActivityFactory.createTransitionFromTo(builder, start, xorJoin);
        BPMNActivityFactory.createTransitionFromTo(builder, xorJoin, node);
        BPMNActivityFactory.createTransitionFromTo(builder, node, xorSplit);
        BPMNActivityFactory.createTransitionFor(builder, xorSplit, end, condition2);
        BPMNActivityFactory.createTransitionFor(builder, xorSplit, xorJoin, condition1);

        // Bootstrap
        Navigator nav = new NavigatorImplMock();
        List<Node> startNodes = new ArrayList<Node>();
        startNodes.add(start);
        ProcessDefinition definition = new ProcessDefinitionImpl(UUID.randomUUID(), "testLoop", startNodes);
        AbstractProcessInstance instance = new ProcessInstanceImpl(definition);
        instance.getContext().setVariable("counter", "0");
        instance.getContext().setVariable("increment", "1");

        // start node for token is set later on
        token = new TokenImpl(null, instance, nav);

        // Set start
        token.setCurrentNode(start);

    }

    /**
     * Tear down.
     */
    @AfterClass
    public void tearDown() {

        token = null;
        start = null;
        end = null;
        xorSplit = null;
        xorJoin = null;
        node = null;

    }
}
