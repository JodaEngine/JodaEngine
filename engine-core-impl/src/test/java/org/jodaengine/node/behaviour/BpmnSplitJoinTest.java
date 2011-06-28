package org.jodaengine.node.behaviour;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.jodaengine.deployment.ProcessDefinitionImporter;
import org.jodaengine.deployment.importer.definition.BpmnXmlImporter;
import org.jodaengine.exception.JodaEngineException;
import org.jodaengine.ext.listener.AbstractTokenListener;
import org.jodaengine.ext.service.ExtensionService;
import org.jodaengine.navigator.NavigatorImplMock;
import org.jodaengine.process.definition.ProcessDefinition;
import org.jodaengine.process.instance.AbstractProcessInstance;
import org.jodaengine.process.instance.ProcessInstance;
import org.jodaengine.process.structure.Node;
import org.jodaengine.process.token.BpmnToken;
import org.jodaengine.process.token.Token;
import org.jodaengine.process.token.builder.BpmnTokenBuilder;
import org.jodaengine.util.ReflectionUtil;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the split and join behaviour.
 */
public class BpmnSplitJoinTest {
    
    private static final String SPLIT_JOIN_PROCESS = "org/jodaengine/node/behaviour/SimpleGateways.bpmn.xml";
    
    /**
     * Tests, that an and-join correctly produces one token, which afterwards is correctly processed.
     * 
     * @throws JodaEngineException test fails
     */
    @Test
    public void testJoinProducesOneToken() throws JodaEngineException {
        
//        DeploymentBuilder deploymentBuilder = jodaEngineServices.getRepositoryService().getDeploymentBuilder();
        
        InputStream bpmnXmlInputStream = ReflectionUtil.getResourceAsStream(SPLIT_JOIN_PROCESS);
        Assert.assertNotNull(bpmnXmlInputStream);
        ProcessDefinitionImporter processDefinitionImporter = new BpmnXmlImporter(bpmnXmlInputStream);
        
        ProcessDefinition definition = processDefinitionImporter.createProcessDefinition();
        Assert.assertEquals(definition.getStartNodes().size(), 1);
        Node startNode = definition.getStartNodes().iterator().next();
        Assert.assertNotNull(startNode);
        
        NavigatorImplMock mockNavigator = new NavigatorImplMock();
        ExtensionService mockExtension = mock(ExtensionService.class);
        
        AbstractTokenListener mockListener = mock(AbstractTokenListener.class);
        
        when(mockExtension.getExtensions(AbstractTokenListener.class)).thenReturn(Arrays.asList(mockListener));
        
        AbstractProcessInstance instance = new ProcessInstance(
            null,
            new BpmnTokenBuilder(mockNavigator, mockExtension));
        BpmnToken token = new BpmnToken(startNode, null, instance, mockNavigator, mockExtension);
        
        token.executeStep();
        token.executeStep();
        token.executeStep();
        
        mockNavigator.flushWorkQueue();
        token.executeStep();
        
        List<Token> splittedTokens = mockNavigator.getWorkQueue();
        Assert.assertEquals(splittedTokens.size(), 2);
        
        
    }
}
