package de.hpi.oryxengine.deployment;

import java.util.UUID;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.hpi.oryxengine.AbstractJodaEngineTest;
import de.hpi.oryxengine.RepositoryService;
import de.hpi.oryxengine.ServiceFactory;
import de.hpi.oryxengine.deployment.importer.RawProcessDefintionImporter;
import de.hpi.oryxengine.exception.DalmatinaRuntimeException;
import de.hpi.oryxengine.exception.DefinitionNotFoundException;
import de.hpi.oryxengine.factory.definition.ProcessDefinitionFactory;
import de.hpi.oryxengine.factory.definition.SimpleProcessDefinitionFactory;
import de.hpi.oryxengine.process.definition.ProcessDefinition;

/**
 * Test class for deploying a {@link ProcessDefinition}.
 */
@ContextConfiguration(locations = "/test.oryxengine.cfg.xml")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class DeployProcessDefintionTest extends AbstractJodaEngineTest {

    private DeploymentBuilder deploymentBuilder = null;
    private RepositoryService repo = null;
    private ProcessDefinition def = null;
    private UUID defID = null;

    @BeforeClass
    public void setUp() {

        repo = ServiceFactory.getRepositoryService();
        deploymentBuilder = repo.getDeploymentBuilder();
        ProcessDefinitionFactory factory = new SimpleProcessDefinitionFactory();
        defID = UUID.randomUUID();
        def = factory.create(defID);

    }

    /**
     * Testing that a deployed {@link ProcessDefinition} can be retrieved by a {@link RepositoryService}.
     */
    @Test
    public void testDeployment()
    throws DefinitionNotFoundException {

        // Best Regards Tom Baeyens
        deploymentBuilder.deployProcessDefinition(new RawProcessDefintionImporter(def));
        Assert.assertEquals(repo.getProcessDefinition(defID), def,
            "The deployed process definition should be avaialable in the repository.");
    }

    /**
     * Deploy two {@link ProcessDefinition ProcessDefinitions} with the same name.
     * 
     * @throws DefinitionNotFoundException
     */
    @Test(expectedExceptions = DalmatinaRuntimeException.class)
    public void testDuplicateDeployment() {

        deploymentBuilder.deployProcessDefinition(new RawProcessDefintionImporter(def));
        deploymentBuilder.deployProcessDefinition(new RawProcessDefintionImporter(def));

        String failureMessage = "Up to this point a DalmatinaRuntimeException should have been raised.";
        Assert.fail(failureMessage);
    }
}