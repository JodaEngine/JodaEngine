package org.jodaengine.process.instantiation.pattern;

import org.jodaengine.ext.service.ExtensionService;
import org.jodaengine.navigator.NavigatorInside;
import org.jodaengine.process.definition.ProcessDefinitionInside;
import org.jodaengine.process.instance.AbstractProcessInstance;
import org.jodaengine.process.instance.ProcessInstance;
import org.jodaengine.process.instantiation.ProcessInstantiationPattern;
import org.jodaengine.process.instantiation.InstantiationPatternContext;
import org.jodaengine.process.instantiation.StartProcessInstantiationPattern;
import org.jodaengine.process.structure.Node;
import org.jodaengine.process.token.Token;
import org.jodaengine.process.token.builder.BpmnTokenBuilder;

/**
 * This pattern encapsulates the default instantiation semantic for BPMN models. This
 * {@link ProcessInstantiationPattern
 * instantionPattern} can be used when the {@link ProcessDefinitionInside process
 * definition} has one dedicated start node. If there are more than one start nodes in the process definition, there
 * will be placed a token on only one of them.
 * 
 * It also implements the {@link StartProcessInstantiationPattern StartInstantiationPattern-Interface}, so that it can
 * be used
 * as one of the first instantiationPattern.
 */
public class DefaultBpmnProcessInstanceCreationPattern extends AbstractProcessInstantiationPattern implements
StartProcessInstantiationPattern {

    @Override
    public AbstractProcessInstance createProcessInstance(InstantiationPatternContext patternContext) {

        ProcessDefinitionInside processDefinition = patternContext.getProcessDefinition();
        NavigatorInside navigator = patternContext.getNavigatorService();
        ExtensionService extensions = patternContext.getExtensionService();

        BpmnTokenBuilder tokenBuilder = new BpmnTokenBuilder(navigator, extensions);
        AbstractProcessInstance processInstance = new ProcessInstance(processDefinition, tokenBuilder);

        // Put only a token on the specified node for start.
        Node startNode = patternContext.getSpecifiedStartNode();
        Token newToken = processInstance.createToken(startNode);
        navigator.addWorkToken(newToken);

        return processInstance;
    }

    @Override
    protected AbstractProcessInstance createProcessInstanceIntern(InstantiationPatternContext patternContext,
                                                                  AbstractProcessInstance previosProcessInstance) {

        if (previosProcessInstance != null) {
            String warnMessage = "The previous pattern already created an ProcessInstance. This one is now overridden.";
            logger.warn(warnMessage);
        }

        // Nevertheless returning the ProcessInstance that would be created originally
        return createProcessInstance(patternContext);
    }
}
