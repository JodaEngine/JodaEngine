package org.jodaengine.eventmanagement.subscription.processevent.intermediate;

import org.jodaengine.eventmanagement.adapter.EventTypes;
import org.jodaengine.eventmanagement.adapter.manual.ManualTriggeringAdapterConfiguration;
import org.jodaengine.eventmanagement.subscription.ProcessEvent;
import org.jodaengine.eventmanagement.subscription.ProcessEventGroup;
import org.jodaengine.eventmanagement.subscription.ProcessIntermediateEvent;
import org.jodaengine.eventmanagement.subscription.condition.simple.TrueEventCondition;
import org.jodaengine.process.token.Token;

/**
 * It is a {@link ProcessEvent} that can only be triggered manually and not automatically.
 */
public class ProcessIntermediateManualTriggeringEvent extends AbstractProcessIntermediateEvent {

    /**
     * Default Constructor.
     * 
     * @param name
     *            - the name of the {@link ManualTriggeringAdapter}
     * @param token
     *            - the {@link Token} that registered this event.
     */
    public ProcessIntermediateManualTriggeringEvent(String name, Token token) {

        this(name, token, null);
    }

    /**
     * Default Constructor.
     * 
     * @param token
     *            - the {@link Token} that registered this event.
     * @param eventGroup
     *            - if this {@link ProcessIntermediateEvent} is related to other {@link ProcessIntermediateEvent} then
     *            the {@link ProcessEventGroup} can be specified here
     */
    public ProcessIntermediateManualTriggeringEvent(String name, Token token, ProcessEventGroup eventGroup) {

        super(EventTypes.ManualTriggered, new ManualTriggeringAdapterConfiguration(name), new TrueEventCondition(),
            token, eventGroup);
    }
}
