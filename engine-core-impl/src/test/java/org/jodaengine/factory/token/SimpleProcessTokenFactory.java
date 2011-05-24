package org.jodaengine.factory.token;

import javax.annotation.Nullable;

import org.jodaengine.ext.service.ExtensionService;
import org.jodaengine.process.instance.ProcessInstanceImpl;
import org.jodaengine.process.structure.Node;
import org.jodaengine.process.token.Token;
import org.jodaengine.process.token.TokenImpl;


/**
 * A factory for creating simple ProcessToken objects. So it creates a Process starting at one node specified.
 */
public class SimpleProcessTokenFactory {
    
    /**
     * Creates the the simple Process Token starting at a given node with a new ProcessInstance.
     *
     * @param startNode the start node
     * @param extensionService the {@link ExtensionService}, to get extensions from
     * @return the process instance
     */
    public Token create(Node startNode,
                        @Nullable ExtensionService extensionService) {
        return new TokenImpl(startNode, new ProcessInstanceImpl(null), null, extensionService);
    }
    
    /**
     * Creates the the simple Process Token starting at a given node with a new ProcessInstance.
     *
     * @param startNode the start node
     * @return the process instance
     */
    public Token create(Node startNode) {
        return create(startNode, null);
    }

}
