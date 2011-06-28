package org.jodaengine.node.behaviour;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import org.jodaengine.deployment.ProcessDefinitionImporter;
import org.jodaengine.deployment.importer.definition.BpmnXmlImporter;
import org.jodaengine.exception.JodaEngineException;
import org.jodaengine.ext.listener.AbstractTokenListener;
import org.jodaengine.ext.listener.token.ActivityLifecycleChangeEvent;
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
import org.mockito.ArgumentCaptor;
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
        Token token = new BpmnToken(startNode, null, instance, mockNavigator, mockExtension);
        
        token.executeStep();
        token.executeStep();
        token.executeStep();
        
        mockNavigator.flushWorkQueue();
        token.executeStep();
        
        // the AND-Split has been executed
        List<Token> splittedTokens = mockNavigator.getWorkQueue();
        Assert.assertEquals(splittedTokens.size(), 2);
        
        Token splitToken1 = splittedTokens.get(0);
        Token splitToken2 = splittedTokens.get(1);
                
        splitToken1.executeStep();
        splitToken2.executeStep();
        
        // empty the queue before the and-join is executed.
        mockNavigator.flushWorkQueue();
        
        splitToken1.executeStep();
        splitToken2.executeStep();
        
        // one token should be on the xor join now
        List<Token> joinedTokens = mockNavigator.getWorkQueue();
        Assert.assertEquals(joinedTokens.size(), 1);
        
        token = joinedTokens.get(0);
        
        Node nextNode = token.getCurrentNode().getOutgoingControlFlows().get(0).getDestination();
        
        ArgumentCaptor<ActivityLifecycleChangeEvent> eventCaptor
        = ArgumentCaptor.forClass(ActivityLifecycleChangeEvent.class);       
        
        // 32 times, because the and-join has been executed twice (state changes included).
        verify(mockListener, times(32)).update(any(Observable.class), eventCaptor.capture());
        Assert.assertEquals(eventCaptor.getAllValues().size(), 32);
        
        

        reset(mockListener);
        mockNavigator.flushWorkQueue();
        token.executeStep();        
        
        eventCaptor = ArgumentCaptor.forClass(ActivityLifecycleChangeEvent.class);       
        verify(mockListener, times(4)).update(any(Observable.class), eventCaptor.capture());
        Assert.assertEquals(eventCaptor.getAllValues().size(), 4);
                
        joinedTokens = mockNavigator.getWorkQueue();
        Assert.assertEquals(joinedTokens.size(), 1);
        Assert.assertEquals(token.getCurrentNode(), nextNode, "Token should be now on node D");
        
        reset(mockListener);
        token.executeStep();
        token.executeStep();        
        
        eventCaptor = ArgumentCaptor.forClass(ActivityLifecycleChangeEvent.class);       
        verify(mockListener, times(7)).update(any(Observable.class), eventCaptor.capture());
        Assert.assertEquals(eventCaptor.getAllValues().size(), 7);
    }
}
