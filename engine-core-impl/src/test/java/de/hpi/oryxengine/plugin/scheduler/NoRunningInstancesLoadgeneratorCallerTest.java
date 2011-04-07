package de.hpi.oryxengine.plugin.scheduler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.hpi.oryxengine.ServiceFactory;
import de.hpi.oryxengine.exception.IllegalStarteventException;
import de.hpi.oryxengine.loadgenerator.LoadGenerator;
import de.hpi.oryxengine.navigator.NavigatorImpl;
import de.hpi.oryxengine.navigator.schedule.FIFOScheduler;
import de.hpi.oryxengine.process.definition.ProcessDefinition;
import de.hpi.oryxengine.process.instance.ProcessInstance;
import de.hpi.oryxengine.process.structure.Node;
import de.hpi.oryxengine.process.token.Token;
import de.hpi.oryxengine.process.token.TokenImpl;
import de.hpi.oryxengine.repository.ProcessRepository;
import de.hpi.oryxengine.repository.RepositorySetup;

/**
 * Tests the SchedulerEmptyListener Plugin, that invokes a method on a loadgenerator when the queue of the Scheduler is.
 * empty
 */
public class NoRunningInstancesLoadgeneratorCallerTest {

    private NavigatorImpl nav = null;
    
    private LoadGenerator mockiGene = null;
    
    private Token pi = null;

    /**
     * Sets the up repo.
     * @throws IllegalStarteventException 
     */
    @BeforeClass
    public void setUpRepo() throws IllegalStarteventException {

        RepositorySetup.fillRepository();
    }

    /**
     * Creates everything that we need (a Scheduler with the Plugin), a processtoken and a mocked loadGenerator.
     *
     * @throws Exception the exception
     */
    @BeforeMethod
    public void setUp() throws Exception {

        nav = new NavigatorImpl();
        mockiGene = mock(LoadGenerator.class);
        NoRunningInstancesLoadgeneratorCaller caller = new NoRunningInstancesLoadgeneratorCaller(mockiGene);
        nav.registerPlugin(caller);
        ProcessRepository repo = ServiceFactory.getRepositoryService();
        ProcessDefinition def = repo.getDefinition(RepositorySetup.FIRST_EXAMPLE_PROCESS_ID);
        List<Node> startNodes = def.getStartNodes();
        Node startNode = startNodes.get(0);
        pi = new TokenImpl(startNode);
    }

    /**
     * Test that the method schedulerIsEmpty() is invoked on load generator.
     */
    @Test
    public void testMethodInvokedOnLoadGenerator() {

        nav.startArbitraryInstance(pi);
        ProcessInstance instance = pi.getInstance();
        nav.signalEndedProcessInstance(instance);
        
        // verify that the method we want is called on the load generator
        verify(mockiGene).navigatorCurrentlyFinished();

    }
}