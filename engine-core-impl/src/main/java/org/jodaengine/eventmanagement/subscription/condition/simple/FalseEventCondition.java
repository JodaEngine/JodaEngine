package org.jodaengine.eventmanagement.subscription.condition.simple;

import org.jodaengine.eventmanagement.AdapterEvent;
import org.jodaengine.eventmanagement.subscription.condition.EventCondition;

/**
 * TODO @EVENTMANAGERTEAM and why again do we need it? - It is a basic condition and we need it for testing.
 * 
 * A Condition that never holds. It always returns false. 
 */
public class FalseEventCondition implements EventCondition {

    @Override
    public boolean evaluate(AdapterEvent adapterEvent) {

        return false;
    }
}
