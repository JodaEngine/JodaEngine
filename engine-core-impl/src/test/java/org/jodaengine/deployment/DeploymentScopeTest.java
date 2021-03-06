package org.jodaengine.deployment;

import java.util.UUID;

import org.jodaengine.RepositoryService;
import org.jodaengine.exception.IllegalStarteventException;
import org.jodaengine.process.activation.ProcessDeActivationPattern;
import org.jodaengine.process.definition.ProcessDefinition;
import org.jodaengine.process.definition.ProcessDefinitionID;
import org.jodaengine.process.definition.bpmn.BpmnProcessDefinitionBuilder;
import org.jodaengine.process.instantiation.StartProcessInstantiationPattern;
import org.jodaengine.util.testing.AbstractJodaEngineTest;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * The Class DeploymentScopeTest.
 */
public class DeploymentScopeTest extends AbstractJodaEngineTest {
    private RepositoryService repository = null;
    private DeploymentBuilder builder = null;
    private BpmnProcessDefinitionBuilder defBuilder = null;
    private ProcessDefinition definition = null;
    private Deployment deployment = null;

    /**
     * Test the removal of the scope of a definition.
     */
    @Test
    public void testRemovalOfScope() {

        repository.deployInNewScope(deployment);

        repository.deleteProcessDefinition(definition.getID());
        Assert.assertNull(repository.getScopeForDefinition(definition.getID()),
            "The scope should not exist (i.e. not be registered) anymore.");
    }

    /**
     * Test the creation of a scope upon deployment.
     */
    @Test
    public void testCreationOfScope() {

        DeploymentScope scope = repository.deployInNewScope(deployment);
        Assert.assertEquals(this.repository.getScopeForDefinition(definition.getID()), scope,
            "The created scope should be set for the deployed process definition.");
    }

    /**
     * Does Setup and creates a deployment ready to deploy.
     *
     * @throws IllegalStarteventException the illegal startevent exception
     */
    @BeforeMethod
    public void setUpAndDeployProcess()
    throws IllegalStarteventException {

        repository = jodaEngineServices.getRepositoryService();
        builder = repository.getDeploymentBuilder();
        defBuilder =BpmnProcessDefinitionBuilder.newBuilder();

        ProcessDefinitionID id = new ProcessDefinitionID(UUID.randomUUID().toString());
        defBuilder.addStartInstantiationPattern(Mockito.mock(StartProcessInstantiationPattern.class));
        defBuilder.addDeActivationPattern(Mockito.mock(ProcessDeActivationPattern.class));

        definition = defBuilder.buildDefinition();
        Whitebox.setInternalState(definition, "id", id);
        builder.addProcessDefinition(definition);
        deployment = builder.buildDeployment();

    }

}
