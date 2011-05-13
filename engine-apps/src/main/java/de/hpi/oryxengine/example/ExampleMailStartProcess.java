package de.hpi.oryxengine.example;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hpi.oryxengine.JodaEngineServices;
import de.hpi.oryxengine.bootstrap.JodaEngine;
import de.hpi.oryxengine.correlation.adapter.EventTypes;
import de.hpi.oryxengine.correlation.adapter.mail.MailAdapterConfiguration;
import de.hpi.oryxengine.correlation.adapter.mail.MailAdapterEvent;
import de.hpi.oryxengine.correlation.registration.EventCondition;
import de.hpi.oryxengine.correlation.registration.EventConditionImpl;
import de.hpi.oryxengine.deployment.DeploymentBuilder;
import de.hpi.oryxengine.deployment.importer.RawProcessDefintionImporter;
import de.hpi.oryxengine.exception.DefinitionNotFoundException;
import de.hpi.oryxengine.exception.IllegalStarteventException;
import de.hpi.oryxengine.navigator.NavigatorImpl;
import de.hpi.oryxengine.node.factory.bpmn.BpmnCustomNodeFactory;
import de.hpi.oryxengine.node.factory.bpmn.BpmnNodeFactory;
import de.hpi.oryxengine.plugin.navigator.NavigatorListenerLogger;
import de.hpi.oryxengine.process.definition.ProcessDefinition;
import de.hpi.oryxengine.process.definition.ProcessDefinitionBuilder;
import de.hpi.oryxengine.process.structure.Node;

/**
 * The Class ExampleMailStartProcess. This is an example process that is started
 * by an arriving email.
 */
public final class ExampleMailStartProcess {

    private static final Logger logger = LoggerFactory.getLogger(ExampleMailStartProcess.class);

    /** Hidden constructor */
    private ExampleMailStartProcess() {

    };

    /**
     * Creating and starting the example process. The example process is
     * triggered by an Email.
     * 
     * @param args
     *            the arguments
     * @throws IllegalStarteventException
     *             thrown if an illegal start event is given
     */
    public static void main(String... args)
    throws IllegalStarteventException {

        String exampleProcessName = "exampleMailProcess";

        // the main
        JodaEngineServices jodaEngineServices = JodaEngine.start();

        ((NavigatorImpl) jodaEngineServices.getNavigatorService())
        .registerPlugin(NavigatorListenerLogger.getInstance());

        // Building the ProcessDefintion
        ProcessDefinitionBuilder builder = jodaEngineServices.getRepositoryService()
                                                             .getDeploymentBuilder()
                                                             .getProcessDefinitionBuilder();

        Node startNode = BpmnCustomNodeFactory.createBpmnNullStartNode(builder);

        // Building Node1
        int[] ints = { 1, 1 };
        Node node1 = BpmnCustomNodeFactory.createBpmnAddNumbersAndStoreNode(builder, "result", ints);

        // Building Node2
        Node node2 = BpmnCustomNodeFactory.createBpmnPrintingVariableNode(builder, "result");

        BpmnNodeFactory.createTransitionFromTo(builder, startNode, node1);
        BpmnNodeFactory.createTransitionFromTo(builder, node1, node2);

        builder.setDescription("description").setName(exampleProcessName);

        // Create a mail adapater event here.
        // TODO @TobiP Could create a builder for this later.
        MailAdapterConfiguration config = MailAdapterConfiguration.dalmatinaGoogleConfiguration();
        EventCondition subjectCondition = null;
        try {
            subjectCondition = new EventConditionImpl(MailAdapterEvent.class.getMethod("getMessageTopic"), "Hallo");
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        List<EventCondition> conditions = new ArrayList<EventCondition>();
        conditions.add(subjectCondition);

        // StartEvent event = new StartEventImpl( EventTypes.Mail,
        // config,
        // conditions,
        // exampleProcessUUID);


            builder.createStartTrigger(EventTypes.Mail, config, conditions, startNode);
            ProcessDefinition def = builder.buildDefinition();

            DeploymentBuilder deploymentBuilder = jodaEngineServices.getRepositoryService().getDeploymentBuilder();
            UUID exampleProcessUUID = deploymentBuilder.deployProcessDefinition(new RawProcessDefintionImporter(def));

            jodaEngineServices.getRepositoryService().activateProcessDefinition(exampleProcessUUID);

        try {
            jodaEngineServices.getNavigatorService().startProcessInstance(exampleProcessUUID);
        } catch (DefinitionNotFoundException definitionNotFoundException) {

            String errorMessage = "An Exception occurred: " + definitionNotFoundException.getMessage();
            logger.error(errorMessage, definitionNotFoundException);
        }

        // Thread.sleep(SLEEP_TIME);

        // navigator.stop();
    }
}
