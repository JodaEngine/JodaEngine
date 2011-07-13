package org.jodaengine.process.token;


import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jodaengine.JodaEngineServices;
import org.jodaengine.deployment.DeploymentBuilder;
import org.jodaengine.exception.DefinitionNotActivatedException;
import org.jodaengine.exception.DefinitionNotFoundException;
import org.jodaengine.exception.IllegalStarteventException;
import org.jodaengine.exception.UnsupportedDefinitionException;
import org.jodaengine.node.factory.PetriNetTransitionFactory;
import org.jodaengine.node.factory.petri.PetriNetNodeFactory;
import org.jodaengine.process.definition.ProcessDefinition;
import org.jodaengine.process.definition.ProcessDefinitionID;
import org.jodaengine.process.definition.petri.PetriNetProcessDefinitionBuilder;
import org.jodaengine.process.instance.AbstractProcessInstance;
import org.jodaengine.process.structure.Node;
import org.jodaengine.util.testing.AbstractJodaEngineTest;
import org.jodaengine.util.testing.JodaEngineTestConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * The Class CompletePetriNetTest. It deploys a petri net and starts instances of it.
 */
@JodaEngineTestConfiguration(configurationFile = "petriNetjodaengine.cfg.xml")
public class CompletePetriNetTest extends AbstractJodaEngineTest{

    private static ProcessDefinitionID sampleProcessUUID;
    private Node startPlace, secondStartPlace, endPlace, firstTransition;

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CompletePetriNetTest.class);


    /**
     * Sets the process up.
     *
     * @throws IllegalStarteventException the illegal startevent exception
     * @throws DefinitionNotFoundException the definition not found exception
     */
    @BeforeMethod
    public void setUp()
    throws IllegalStarteventException, DefinitionNotFoundException {
        sampleProcessUUID = deploySampleProcess(jodaEngineServices);

    }
    
    /**
     * Sets the process down.
     */
    @AfterMethod
    public void setDown() {
        sampleProcessUUID = null;
    }
    
    /**
     * Test one process execution.
     *
     * @throws DefinitionNotFoundException the definition not found exception
     * @throws InterruptedException the interrupted exception
     * @throws DefinitionNotActivatedException 
     * @throws UnsupportedDefinitionException 
     */
    @Test
    public void testTheProcessExecution() throws DefinitionNotFoundException, InterruptedException, DefinitionNotActivatedException, UnsupportedDefinitionException {
        
        AbstractProcessInstance processInstance = jodaEngineServices.getNavigatorService().startProcessInstance(
            sampleProcessUUID);

        Thread.sleep(1000);

        assertEquals(processInstance.getAssignedTokens().size(), 1);
        assertEquals(processInstance.getAssignedTokens().get(0).getCurrentNode(), endPlace);
        
        
    }
    
    /**
     * Test two process executions.
     *
     * @throws DefinitionNotFoundException the definition not found exception
     * @throws InterruptedException the interrupted exception
     * @throws DefinitionNotActivatedException 
     * @throws UnsupportedDefinitionException 
     */
    @Test
    public void testTheProcessExecutionOfTwo() throws DefinitionNotFoundException, InterruptedException, DefinitionNotActivatedException, UnsupportedDefinitionException {
        int counter = 0;
        
        AbstractProcessInstance processInstance = jodaEngineServices.getNavigatorService().startProcessInstance(
            sampleProcessUUID);
        
        AbstractProcessInstance processInstanceTwo = jodaEngineServices.getNavigatorService().startProcessInstance(
            sampleProcessUUID);

        // Anti Waiting
        while(counter < 5000) {
            
            Thread.sleep(500);
            if((processInstance.getAssignedTokens().size() == 1) && (processInstanceTwo.getAssignedTokens().size() == 1)) {
                break;
            } else {
                counter = counter+500;
            }

        }
        assertEquals(processInstance.getAssignedTokens().size(), 1);
        assertEquals(processInstance.getAssignedTokens().get(0).getCurrentNode(), endPlace);
        
        assertEquals(processInstanceTwo.getAssignedTokens().size(), 1);
        assertEquals(processInstanceTwo.getAssignedTokens().get(0).getCurrentNode(), endPlace);
        
        
    }

    /**
     * Deploys the sample process.
     *
     * @param jodaEngineServices the joda engine services
     * @return the process definition id
     * @throws IllegalStarteventException the illegal startevent exception
     */
    private ProcessDefinitionID deploySampleProcess(JodaEngineServices jodaEngineServices)
    throws IllegalStarteventException {

        DeploymentBuilder deploymentBuilder = jodaEngineServices.getRepositoryService().getDeploymentBuilder();

        ProcessDefinition processDefinition = buildSampleProcessDefinition(PetriNetProcessDefinitionBuilder.newBuilder());

        ProcessDefinitionID sampleProcessUUID = processDefinition.getID();
        deploymentBuilder.addProcessDefinition(processDefinition);
        
        jodaEngineServices.getRepositoryService().deployInNewScope(deploymentBuilder.buildDeployment());

        return sampleProcessUUID;

    }

    /**
     * Builds the {@link ProcessDefinition} for the sample petri net.
     * <p>
     * @param definitionBuilder the definition builder
     * @return the process definition
     * @throws IllegalStarteventException the illegal startevent exception
     * </p>
     */
    private ProcessDefinition buildSampleProcessDefinition(PetriNetProcessDefinitionBuilder definitionBuilder)
    throws IllegalStarteventException {

        String sampleProcessName = "Sample petri net";
        String sampleProcessDescription = "A simple petri net.";

        startPlace = PetriNetNodeFactory.createPlace();
        startPlace.setAttribute("name", "S0");
        secondStartPlace = PetriNetNodeFactory.createPlace();
        secondStartPlace.setAttribute("name", "S1");
        endPlace = PetriNetNodeFactory.createPlace();
        endPlace.setAttribute("name", "S3");
        firstTransition = PetriNetNodeFactory.createPetriTransition();
        firstTransition.setAttribute("name", "T2");


        PetriNetTransitionFactory.createControlFlowFromTo(definitionBuilder, startPlace, firstTransition);
        PetriNetTransitionFactory.createControlFlowFromTo(definitionBuilder, secondStartPlace, firstTransition);
        PetriNetTransitionFactory.createControlFlowFromTo(definitionBuilder, firstTransition, endPlace);
        List<Node> startNodes = new ArrayList<Node>();
        startNodes.add(startPlace);
        startNodes.add(secondStartPlace);

        definitionBuilder.setName(sampleProcessName).setDescription(sampleProcessDescription).setStartNodes(startNodes);

        return definitionBuilder.buildDefinition();
    }
}
