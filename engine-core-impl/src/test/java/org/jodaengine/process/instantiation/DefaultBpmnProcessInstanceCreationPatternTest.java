package org.jodaengine.process.instantiation;

import java.util.ArrayList;
import java.util.List;

import org.jodaengine.eventmanagement.processevent.incoming.IncomingStartProcessEvent;
import org.jodaengine.process.definition.ProcessDefinition;
import org.jodaengine.process.definition.ProcessDefinitionInside;
import org.jodaengine.process.instance.AbstractProcessInstance;
import org.jodaengine.process.instantiation.pattern.DefaultBpmnProcessInstanceCreationPattern;
import org.jodaengine.process.structure.Node;
import org.jodaengine.util.testing.AbstractJodaEngineTest;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the class {@link DefaultBpmnProcessInstanceCreationPattern}.
 */
public class DefaultBpmnProcessInstanceCreationPatternTest extends AbstractJodaEngineTest {

    private DefaultBpmnProcessInstanceCreationPattern pattern;
    private InstantiationPatternContext patternContext;
    private Node startNode;
    private Node otherStartNode;
    private ProcessDefinitionInside processDefinition;

    @BeforeMethod
    public void setUp() {

        pattern = new DefaultBpmnProcessInstanceCreationPattern();
        startNode = Mockito.mock(Node.class);
        otherStartNode = Mockito.mock(Node.class);

        List<Node> startNodes = new ArrayList<Node>();
        startNodes.add(startNode);
        startNodes.add(otherStartNode);
        processDefinition = Mockito.mock(ProcessDefinitionInside.class);
        Mockito.when(processDefinition.getStartNodes()).thenReturn(startNodes);
    }

    /**
     * Test the instance creation with a supplied start node.
     */
    @Test
    public void testWithStartNode() {

        patternContext = new InstantiationPatternContextImpl(processDefinition, startNode);
        AbstractProcessInstance processInstance = pattern.createProcessInstance(patternContext);

        Assert.assertEquals(processInstance.getAssignedTokens().size(), 1);
        Assert.assertEquals(processInstance.getAssignedTokens().get(0).getCurrentNode(), startNode);
    }

    /**
     * We write another test for the other start node to ensure, that the pattern uses the supplied start node and not
     * just the first one of the definition's start nodes.
     */
    @Test
    public void testWithOtherStartNode() {

        patternContext = new InstantiationPatternContextImpl(processDefinition, otherStartNode);
        AbstractProcessInstance processInstance = pattern.createProcessInstance(patternContext);

        Assert.assertEquals(processInstance.getAssignedTokens().size(), 1);
        Assert.assertEquals(processInstance.getAssignedTokens().get(0).getCurrentNode(), otherStartNode);
    }
}
