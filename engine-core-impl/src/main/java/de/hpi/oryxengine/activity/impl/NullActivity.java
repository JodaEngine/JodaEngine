package de.hpi.oryxengine.activity.impl;

import de.hpi.oryxengine.activity.AbstractActivity;
import de.hpi.oryxengine.process.token.Token;

/**
 * The Class StartActivity.
 * The now empty behaviour of a for instance startnode.
 */
public class NullActivity
extends AbstractActivity {

    /**
     * Default constructor. Creates a new start activity.
     */
    public NullActivity() {
        super();
    }

    @Override
    public void executeIntern(Token instance) {
        // TODO what is required here?
        // Nothing toDo
    }

}