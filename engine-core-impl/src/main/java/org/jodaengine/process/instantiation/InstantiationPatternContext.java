package org.jodaengine.process.instantiation;

import org.jodaengine.eventmanagement.processevent.incoming.IncomingStartProcessEvent;
import org.jodaengine.process.definition.ProcessDefinitionInside;
import org.jodaengine.process.structure.Node;
import org.jodaengine.util.ServiceContext;


/**
 * Extends the {@link ServiceContext} with new methods especially for the {@link ProcessInstantiationPattern}-Chain.
 */
public interface InstantiationPatternContext extends ServiceContext {

    /**
     * Gets the {@link ProcessDefinitionInside processDefinition}.
     * 
     * @return the {@link ProcessDefinitionInside}
     */
    ProcessDefinitionInside getProcessDefinition();

    /**
     * In case an {@link IncomingStartProcessEvent startEvent} was thrown (starting the {@link ProcessInstantiationPattern})
     * then this method returns the thrown event.
     * 
     * @return the thrown {@link IncomingStartProcessEvent}
     */
    IncomingStartProcessEvent getThrownStartEvent();
    
    /**
     * In case an {@link Node startNode} has been specified, this method returns it.
     * Otherwise returns null.
     * 
     * @return the thrown {@link IncomingStartProcessEvent}
     */
    Node getSpecifiedStartNode();
}
