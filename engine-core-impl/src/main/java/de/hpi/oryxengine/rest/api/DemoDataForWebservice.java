package de.hpi.oryxengine.rest.api;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hpi.oryxengine.ServiceFactory;
import de.hpi.oryxengine.activity.impl.BPMNActivityFactory;
import de.hpi.oryxengine.allocation.AllocationStrategies;
import de.hpi.oryxengine.allocation.Form;
import de.hpi.oryxengine.allocation.Task;
import de.hpi.oryxengine.deployment.DeploymentBuilder;
import de.hpi.oryxengine.deployment.importer.RawProcessDefintionImporter;
import de.hpi.oryxengine.exception.DefinitionNotFoundException;
import de.hpi.oryxengine.exception.IllegalStarteventException;
import de.hpi.oryxengine.process.definition.ProcessDefinitionBuilderImpl;
import de.hpi.oryxengine.process.definition.ProcessDefinition;
import de.hpi.oryxengine.process.definition.ProcessDefinitionBuilder;
import de.hpi.oryxengine.process.structure.Node;
import de.hpi.oryxengine.resource.AbstractParticipant;
import de.hpi.oryxengine.resource.AbstractRole;
import de.hpi.oryxengine.resource.IdentityBuilder;
import de.hpi.oryxengine.resource.allocation.AllocationStrategiesImpl;
import de.hpi.oryxengine.resource.allocation.FormImpl;
import de.hpi.oryxengine.resource.allocation.TaskImpl;
import de.hpi.oryxengine.resource.allocation.pattern.RolePushPattern;
import de.hpi.oryxengine.resource.allocation.pattern.SimplePullPattern;

/**
 * The Class DemoDataForWebservice generates some example data when called.
 */

public final class DemoDataForWebservice {

    private static final String PATH_TO_WEBFORMS = "src/main/resources/forms";
    private static IdentityBuilder builder;
    private static AbstractRole r;
    public final static int NUMBER_OF_PROCESSINSTANCES = 10;
    private static boolean invoked = false;

    /**
     * Instantiates a new demo data for webservice.
     */
    private DemoDataForWebservice() {

    }

    /**
     * Resets invoked, to be honest mostly for testign purposed after each method.
     */
    public synchronized static void resetInvoked() {

        invoked = false;
    }

    /**
     * Gets the builder.
     * 
     * @return the builder
     */
    private static IdentityBuilder getBuilder() {

        builder = ServiceFactory.getIdentityService().getIdentityBuilder();
        return builder;
    }

    /**
     * Generate example Participants.
     */
    public static synchronized void generate() {

        if (!invoked) {
            invoked = true;
            generateDemoParticipants();
            try {
                generateDemoWorklistItems();
            } catch (DefinitionNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalStarteventException e) {
                e.printStackTrace();
            }
        }
        
    }

    /**
     * Generate demo participants.
     */
    private static void generateDemoParticipants() {

        r = getBuilder().createRole("BPT");
        AbstractParticipant p1 = getBuilder().createParticipant("Thorben");
        AbstractParticipant p2 = getBuilder().createParticipant("Tobi P.");
        AbstractParticipant p3 = getBuilder().createParticipant("Tobi M.");
        AbstractParticipant p4 = getBuilder().createParticipant("Gerardo");
        AbstractParticipant p5 = getBuilder().createParticipant("Jan");
        AbstractParticipant p6 = getBuilder().createParticipant("Jannik");
        getBuilder().participantBelongsToRole(p1.getID(), r.getID()).participantBelongsToRole(p2.getID(), r.getID())
        .participantBelongsToRole(p3.getID(), r.getID()).participantBelongsToRole(p4.getID(), r.getID())
        .participantBelongsToRole(p5.getID(), r.getID()).participantBelongsToRole(p6.getID(), r.getID());

    }

    /**
     * Generate demo worklist items for our participants.
     * 
     * @throws IllegalStarteventException
     * @throws DefinitionNotFoundException
     */
    private static void generateDemoWorklistItems()
    throws IllegalStarteventException, DefinitionNotFoundException {

        // TODO Use Example Process to create some tasks for the role demo

        ProcessDefinitionBuilder builder = new ProcessDefinitionBuilderImpl();

        Node startNode, node1, node2, node3, endNode;

        startNode = BPMNActivityFactory.createBPMNStartEventNode(builder);

        // Creating the Webform for the task
        DeploymentBuilder deploymentBuilder = ServiceFactory.getRepositoryService().getDeploymentBuilder();
        UUID processArtifactID = deploymentBuilder.deployArtifactAsFile("form1", new File(PATH_TO_WEBFORMS + "/claimPoints.html"));
        Form form = new FormImpl(ServiceFactory.getRepositoryService().getProcessResource(processArtifactID));

        // Create the task
        AllocationStrategies strategies = new AllocationStrategiesImpl(new RolePushPattern(), new SimplePullPattern(),
            null, null);
        Task task = new TaskImpl("do something", "Really do something we got a demo coming up guys!", form, strategies,
            r);

        node1 = BPMNActivityFactory.createBPMNUserTaskNode(builder, task);

        node2 = BPMNActivityFactory.createBPMNUserTaskNode(builder, task);

        // Create the task
        node3 = BPMNActivityFactory.createBPMNUserTaskNode(builder, task);

        endNode = BPMNActivityFactory.createBPMNEndEventNode(builder);

        BPMNActivityFactory.createTransitionFromTo(builder, startNode, node1);
        BPMNActivityFactory.createTransitionFromTo(builder, node1, node2);
        BPMNActivityFactory.createTransitionFromTo(builder, node2, node3);
        BPMNActivityFactory.createTransitionFromTo(builder, node3, endNode);

        // Start Process
        ProcessDefinition processDefinition = builder.setName("Demoprocess")
        .setDescription("A simple demo process with three human tasks.").buildDefinition();

        UUID processID = ServiceFactory.getRepositoryService().getDeploymentBuilder()
        .deployProcessDefinition(new RawProcessDefintionImporter(processDefinition));

        // Tobi wants more tasks
        for (int i = 0; i < NUMBER_OF_PROCESSINSTANCES; i++) {
            ServiceFactory.getNavigatorService().startProcessInstance(processID);
        }

    }
}
