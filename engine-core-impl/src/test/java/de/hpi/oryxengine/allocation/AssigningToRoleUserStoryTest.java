package de.hpi.oryxengine.allocation;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.hpi.oryxengine.ServiceFactory;
import de.hpi.oryxengine.ServiceFactoryForTesting;
import de.hpi.oryxengine.activity.AbstractActivity;
import de.hpi.oryxengine.activity.Activity;
import de.hpi.oryxengine.activity.impl.EndActivity;
import de.hpi.oryxengine.activity.impl.HumanTaskActivity;
import de.hpi.oryxengine.allocation.pattern.RolePushPattern;
import de.hpi.oryxengine.allocation.pattern.SimplePullPattern;
import de.hpi.oryxengine.exception.DalmatinaException;
import de.hpi.oryxengine.factory.node.GerardoNodeFactory;
import de.hpi.oryxengine.factory.resource.ParticipantFactory;
import de.hpi.oryxengine.navigator.NavigatorImplMock;
import de.hpi.oryxengine.process.instance.ProcessInstanceImpl;
import de.hpi.oryxengine.process.structure.Node;
import de.hpi.oryxengine.process.token.Token;
import de.hpi.oryxengine.process.token.TokenImpl;
import de.hpi.oryxengine.resource.IdentityBuilder;
import de.hpi.oryxengine.resource.Participant;
import de.hpi.oryxengine.resource.Role;
import de.hpi.oryxengine.resource.worklist.WorklistItem;
import de.hpi.oryxengine.resource.worklist.WorklistItemState;

/**
 * This test assigns a task to a role or to a resource that contains other resources.
 */
@ContextConfiguration(locations = "/test.oryxengine.cfg.xml")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AssigningToRoleUserStoryTest extends AbstractTestNGSpringContextTests {

    private Token token = null;
    private Role hamburgGuysRole = null;
    private Role mecklenRole = null;
    private Participant jannik = null;
    private Node endNode = null;
    private Participant gerardo = null;
    private Participant tobi = null;
    private Participant tobi2 = null;

    /**
     * Setup.
     */
    @BeforeMethod
    public void setUp() {

        // The organization structure is already prepared in the factory
        // There is role containing Gerardo and Jannik
        gerardo = ParticipantFactory.createGerardo();
        jannik = ParticipantFactory.createJannik();
        tobi = ParticipantFactory.createTobi();
        tobi2 = ParticipantFactory.createTobi2();
        
        IdentityBuilder identityBuilder = ServiceFactory.getIdentityService().getIdentityBuilder();
        hamburgGuysRole = identityBuilder.createRole("hamburgGuys");
        mecklenRole = identityBuilder.createRole("Mecklenburger");
        
        // look out all these methods are are called on the identity builder (therefore the format)
        identityBuilder
            .participantBelongsToRole(jannik.getID(), hamburgGuysRole.getID())
            .participantBelongsToRole(gerardo.getID(), hamburgGuysRole.getID())
            .participantBelongsToRole(tobi2.getID(), mecklenRole.getID());

        Pattern pushPattern = new RolePushPattern();
        Pattern pullPattern = new SimplePullPattern();

        AllocationStrategies allocationStrategies = new AllocationStrategiesImpl(pushPattern, pullPattern, null, null);

        Task task = new TaskImpl("Clean the office.", "It is very dirty.", allocationStrategies, hamburgGuysRole);

        Activity humanTaskActivity = new HumanTaskActivity(task);
        Node humanTaskNode = GerardoNodeFactory.createSimpleNodeWith(humanTaskActivity);

        AbstractActivity endactivity = new EndActivity();
        endNode = GerardoNodeFactory.createSimpleNodeWith(endactivity);

        humanTaskNode.transitionTo(endNode);

        token = new TokenImpl(humanTaskNode, new ProcessInstanceImpl(null), new NavigatorImplMock());
    }

    /**
     * Tear down.
     */
    @AfterMethod
    public void tearDown() {

        ServiceFactoryForTesting.clearWorklistManager();
        ServiceFactoryForTesting.clearIdentityService();
    }

    /**
     * Test receive work item.
     * 
     * @throws DalmatinaException test fails
     */
    @Test
    public void testHamburgGuysReceiveWorkItem()
    throws DalmatinaException {

        token.executeStep();

        List<WorklistItem> worklistItemsForHamburgGuys =
            ServiceFactory.getWorklistService().getWorklistItems(hamburgGuysRole);
        Assert.assertTrue(worklistItemsForHamburgGuys.size() == 1);

        List<WorklistItem> worklistItemsForJannik = ServiceFactory.getWorklistService().getWorklistItems(jannik);
        Assert.assertTrue(worklistItemsForJannik.size() == 1);
        
        List<WorklistItem> worklistItemsForGerardo = ServiceFactory.getWorklistService().getWorklistItems(gerardo);
        Assert.assertTrue(worklistItemsForGerardo.size() == 1);
        
        // Tobi doesn't belong to any role so he shouldn't have anything to do
        List<WorklistItem> worklistItemsForTobi = ServiceFactory.getWorklistService().getWorklistItems(tobi);
        Assert.assertTrue(worklistItemsForTobi.isEmpty());
        
        // Tobi2 doesn't belong to the HamburgGuysRole so he shouldn't have anything to do
        List<WorklistItem> worklistItemsForTobi2 = ServiceFactory.getWorklistService().getWorklistItems(tobi);
        Assert.assertTrue(worklistItemsForTobi2.isEmpty());
        
        WorklistItem worklistItemForHamburgGuy = worklistItemsForHamburgGuys.get(0);
        Assert.assertEquals(worklistItemForHamburgGuy.getStatus(), WorklistItemState.OFFERED);

        WorklistItem worklistItemForJannik = worklistItemsForHamburgGuys.get(0);
        Assert.assertEquals(worklistItemForJannik.getStatus(), WorklistItemState.OFFERED);
        
        Assert.assertEquals(worklistItemForJannik, worklistItemForHamburgGuy);
    }
    
    /**
     * Test work item claim.
     * 
     * @throws DalmatinaException test fails
     */
    @Test
    public void testJannikClaimsWorklistItem()
    throws DalmatinaException {
     
        token.executeStep();
        
        List<WorklistItem> worklistItemsForHamburgGuys =
            ServiceFactory.getWorklistService().getWorklistItems(hamburgGuysRole);
        WorklistItem worklistItemForHamburgGuy = worklistItemsForHamburgGuys.get(0);
        Assert.assertEquals(worklistItemForHamburgGuy.getStatus(), WorklistItemState.OFFERED);
        
        ServiceFactory.getWorklistService().claimWorklistItemBy(worklistItemForHamburgGuy, jannik);
        assertEquals(worklistItemForHamburgGuy.getStatus(), WorklistItemState.ALLOCATED);
        
        worklistItemsForHamburgGuys = ServiceFactory.getWorklistService().getWorklistItems(hamburgGuysRole);
        Assert.assertEquals(worklistItemsForHamburgGuys.size(), 0);
    }
    
    /**
     * Test that Jannik begins the work on the work item.
     * 
     * @throws DalmatinaException test fails
     */
    @Test
    public void testJannikBeginsWorklistItem()
    throws DalmatinaException {

        token.executeStep();
        
        List<WorklistItem> worklistItemsForHamburgGuys =
            ServiceFactory.getWorklistService().getWorklistItems(hamburgGuysRole);
        WorklistItem worklistItemForHamburgGuy = worklistItemsForHamburgGuys.get(0);
        
        ServiceFactory.getWorklistService().claimWorklistItemBy(worklistItemForHamburgGuy, jannik);
        
        ServiceFactory.getWorklistService().beginWorklistItemBy(worklistItemForHamburgGuy, jannik);
        assertEquals(worklistItemForHamburgGuy.getStatus(), WorklistItemState.EXECUTING);
    }
    
    /**
     * Test the case that Jannik completes the work item.
     * 
     * @throws DalmatinaException test fails
     */
    @Test
    public void testJannikCompletesTheWorkItem()
    throws DalmatinaException {

        token.executeStep();
        
        List<WorklistItem> worklistItemsForHamburgGuys =
            ServiceFactory.getWorklistService().getWorklistItems(hamburgGuysRole);
        WorklistItem worklistItemForHamburgGuy = worklistItemsForHamburgGuys.get(0);
        
        ServiceFactory.getWorklistService().claimWorklistItemBy(worklistItemForHamburgGuy, jannik);        
        ServiceFactory.getWorklistService().beginWorklistItemBy(worklistItemForHamburgGuy, jannik);

        ServiceFactory.getWorklistService().completeWorklistItemBy(worklistItemForHamburgGuy, jannik);
        assertEquals(worklistItemForHamburgGuy.getStatus(), WorklistItemState.COMPLETED);
        
        worklistItemsForHamburgGuys = ServiceFactory.getWorklistService().getWorklistItems(hamburgGuysRole);
        String failureMessage = "Jannik should have completed the task."
                           + "So there should be no item in his worklist and in the worklist of the Role HamburgGuys.";
        Assert.assertTrue(ServiceFactory.getWorklistService().getWorklistItems(jannik).size() == 0, failureMessage);
        Assert.assertTrue(worklistItemsForHamburgGuys.size() == 0, failureMessage);
    }

    /**
     * Test work item resume.
     * 
     * @throws DalmatinaException test fails
     */
    @Test
    public void testResumptionOfProcess()
    throws DalmatinaException {

        token.executeStep();
        
        List<WorklistItem> worklistItemsForHamburgGuys =
            ServiceFactory.getWorklistService().getWorklistItems(hamburgGuysRole);
        WorklistItem worklistItemForHamburgGuy = worklistItemsForHamburgGuys.get(0);
        
        ServiceFactory.getWorklistService().claimWorklistItemBy(worklistItemForHamburgGuy, jannik);
        ServiceFactory.getWorklistService().beginWorklistItemBy(worklistItemForHamburgGuy, jannik);

        ServiceFactory.getWorklistService().completeWorklistItemBy(worklistItemForHamburgGuy, jannik);

        String failureMessage = "Token should point to the endNode, but it points to "
                                + token.getCurrentNode().getID() + ".";
        assertEquals(endNode, token.getCurrentNode(), failureMessage);
    }
}
