package org.jodaengine.eventmanagement.subscription.processeventgroup.intermediate;

import java.util.ArrayList;
import java.util.List;

import org.jodaengine.eventmanagement.subscription.ProcessEvent;
import org.jodaengine.eventmanagement.subscription.ProcessIntermediateEvent;
import org.jodaengine.eventmanagement.subscription.TriggeringBehaviour;
import org.jodaengine.process.token.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class groups several events together to a logical unit. If the {@link ProcessEvent} is connected to another
 * {@link ProcessEvent} than a {@link AbstractProcessIntermediateEventGroup} can be used to specify that connection.
 */
public abstract class AbstractProcessIntermediateEventGroup implements TriggeringBehaviour {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Token token;
    private List<ProcessIntermediateEvent> intermediateEvents;
    protected boolean called = false;

    /**
     * Default Constructor.
     * 
     * @param token
     */
    protected AbstractProcessIntermediateEventGroup(Token token) {

        this.token = token;
    }

    /**
     * Adding {@link ProcessIntermediateEvent}s to this {@link TriggeringBehaviour}.
     * 
     * @param processIntermediateEvent
     *            - the {@link ProcessIntermediateEvent} that should be added to this group
     */
    public void add(ProcessIntermediateEvent processIntermediateEvent) {

        getIntermediateEvents().add(processIntermediateEvent);
    }

    @Override
    public synchronized void trigger(ProcessEvent processEvent) {

        // If it was already called then then leave right now
        if (called) {
            return;
        }

        triggerIntern((ProcessIntermediateEvent) processEvent);
    }

    /**
     * If an {@link ProcessEvent} that belongs to that {@link TriggeringBehaviour} is triggered than this method is
     * called.
     * 
     * @param processIntermediateEvent
     *            - the {@link ProcessIntermediateEvent} that was triggered
     */
    protected abstract void triggerIntern(ProcessIntermediateEvent processIntermediateEvent);

    /**
     * Getter for Lazy initialized {@link AbstractProcessIntermediateEventGroup#intermediateEvents}.
     * 
     * @return a {@link List} of {@link ProcessIntermediateEvent}s
     */
    protected List<ProcessIntermediateEvent> getIntermediateEvents() {

        if (intermediateEvents == null) {
            intermediateEvents = new ArrayList<ProcessIntermediateEvent>();
        }
        return intermediateEvents;
    }
}