package org.jodaengine.process.token;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jodaengine.exception.JodaEngineException;
import org.jodaengine.exception.JodaEngineRuntimeException;
import org.jodaengine.exception.NoValidPathException;
import org.jodaengine.ext.listener.JoinListener;
import org.jodaengine.ext.listener.SplitListener;
import org.jodaengine.ext.listener.TokenCreationListener;
import org.jodaengine.ext.listener.token.ActivityLifecycleChangeEvent;
import org.jodaengine.ext.service.ExtensionService;
import org.jodaengine.navigator.Navigator;
import org.jodaengine.node.activity.Activity;
import org.jodaengine.node.activity.ActivityState;
import org.jodaengine.process.instance.AbstractProcessInstance;
import org.jodaengine.process.structure.ControlFlow;
import org.jodaengine.process.structure.Node;

/**
 * The ancient Bpmn Token class, which is used for processing a bpmn model.
 * Former it was known as TokenImpl, but due to the wish to support mutiple modelling languages
 * it was renamed.
 */
public class BpmnToken extends AbstractToken {

    private ActivityState currentActivityState = null;

    /**
     * Hidden Constructor.
     */
    protected BpmnToken() { }

    /**
     * Instantiates a new process {@link Token}. This will not register any available extension.
     * 
     * TODO remove this constructor - it is for tests only
     * 
     * @param startNode
     *            the start node
     * @param instance
     *            the instance
     * @param navigator
     *            the navigator
     * @deprecated to be removed
     */
    @Deprecated
    public BpmnToken(@Nonnull Node startNode,
                     @Nonnull AbstractProcessInstance instance,
                     @Nonnull Navigator navigator) {
        
        this(startNode, null, null, instance, navigator, null);
    }

    /**
     * Instantiates a new process {@link TokenImpl} and register all available extensions.
     * 
     * @param startNode
     *            the start node
     * @param instance
     *            the instance
     * @param navigator
     *            the navigator
     * @param extensionService
     *            the extension service
     */
    public BpmnToken(@Nonnull Node startNode,
                     @Nonnull AbstractProcessInstance instance,
                     @Nonnull Navigator navigator,
                     @Nullable ExtensionService extensionService) {
        
        this(startNode, null, null, instance, navigator, extensionService);
    }
    /**
     * Instantiates a new process {@link TokenImpl} and registers all available extensions.
     * This constructor is used for child tokens.
     * 
     * @param startNode
     *            the start node
     * @param lastTakenControlFlow
     *            the last taken control flow, e.g. after a split
     * @param parentToken
     *            the parent token
     * @param instance
     *            the instance
     * @param navigator
     *            the navigator
     * @param extensionService
     *            the extension service
     */
    public BpmnToken(@Nonnull Node startNode,
                     @Nullable ControlFlow lastTakenControlFlow,
                     @Nullable Token parentToken,
                     @Nonnull AbstractProcessInstance instance,
                     @Nonnull Navigator navigator,
                     @Nullable ExtensionService extensionService) {
        
        super(startNode, parentToken, instance, navigator, extensionService);
        this.lastTakenControlFlow = lastTakenControlFlow;
        changeActivityState(ActivityState.INIT);
    }

    @Override
    public ActivityState getCurrentActivityState() {

        return currentActivityState;
    }

    @Override
    public void suspend() {

        changeActivityState(ActivityState.WAITING);
        navigator.addSuspendToken(this);
    }

    @Override
    public void resume(Object resumeObject) {

        navigator.removeSuspendToken(this);

        try {
            resumeAndCompleteExecution(resumeObject);
        } catch (NoValidPathException nvpe) {
            exceptionHandler.processException(nvpe, this);
        }
    }

    /**
     * Completes the execution of the activity.
     * 
     * @throws NoValidPathException
     *             thrown if there is no valid path to be executed
     */
    private void completeExecution()
    throws NoValidPathException {
        
        changeActivityState(ActivityState.COMPLETED);
        
        Collection<Token> splittedTokens = getCurrentNode().getOutgoingBehaviour().split(getJoinedTokens());
        
        //
        // split performed, so tell it all our listeners
        //
        for (SplitListener listener: this.splitListener) {
            try {
                listener.splitPerformed(this, this.currentNode, getJoinedTokens(), splittedTokens);
            } catch (Exception e) {
                this.logger.warn("Execution of split listener " + listener.toString() + " failed", e);
            }
        }
        
        for (Token token : splittedTokens) {
            navigator.addWorkToken(token);
        }
        
        internalVariables = null;
        joinedTokens = null;
    }

    @Override
    public void executeStep()
    throws JodaEngineException {
        
        if (this.instance.isCancelled()) {
            // the following statement was already called, when instance.cancel() was called. Nevertheless, a token
            // currently in execution might have created new tokens during split that were added to the instance.
            this.instance.getAssignedTokens().clear();
            return;
        }
        
        changeActivityState(ActivityState.READY);
        
        try {
            
            this.joinedTokens = getCurrentNode().getIncomingBehaviour().join(this);
            
            // only execute any activity behaviour, if the join produced tokens.
            if (!joinedTokens.isEmpty()) {
                //
                // join performed, so tell it all our listeners
                //
                for (JoinListener listener: this.joinListener) {
                    //
                    // TODO change this to be a list of ALL incoming tokens instead only the last one
                    //
                    try {
                        listener.joinPerformed(this, this.currentNode, this.joinedTokens);
                    } catch (Exception e) {
                        this.logger.warn("Execution of join listener " + listener.toString() + " failed", e);
                    }
                }
                
                changeActivityState(ActivityState.ACTIVE);

                Activity currentActivityBehavior = currentNode.getActivityBehaviour();
                currentActivityBehavior.execute(this);

                // Aborting the further execution of the process by the token, because it was suspended
                if (this.currentActivityState == ActivityState.WAITING) {
                    return;
                }

            }
            completeExecution();
        } catch (JodaEngineRuntimeException exception) {
            exceptionHandler.processException(exception, this);
        }
    }

    @Override
    public void cancelExecution() {

        if (this.currentActivityState == ActivityState.ACTIVE || this.currentActivityState == ActivityState.WAITING) {
            Activity currentActivityBehavior = currentNode.getActivityBehaviour();
            currentActivityBehavior.cancel(this);
        }

    }

    /**
     * Changes the state of the activity that the token currently points to.
     * 
     * @param newState
     *            the new state
     */
    protected void changeActivityState(ActivityState newState) {
        
        final ActivityState prevState = currentActivityState;
        
        this.currentActivityState = newState;
        setChanged();
        
        notifyObservers(new ActivityLifecycleChangeEvent(currentNode, prevState, newState, this));
    }

    @Override
    public Collection<Token> navigateTo(Collection<ControlFlow> controlFlowList) {
        
        Collection<Token> tokensToNavigate = new ArrayList<Token>();
        
        //
        // zero outgoing {@link ControlFlow}s
        //
        if (controlFlowList.isEmpty()) {
            
            this.exceptionHandler.processException(new NoValidPathException(), this);
            
            //
            // one outgoing {@link ControlFlow}
            //
        } else if (controlFlowList.size() == 1) {
            
            ControlFlow controlFlow = controlFlowList.iterator().next();
            Node node = controlFlow.getDestination();
            this.setCurrentNode(node);
            this.lastTakenControlFlow = controlFlow;
            changeActivityState(ActivityState.INIT);
            tokensToNavigate.add(this);
            
            //
            // multiple outgoing {@link ControlFlow}s
            //
        } else {
            
            for (ControlFlow controlFlow : controlFlowList) {
                Node node = controlFlow.getDestination();
                Token newToken = createToken(node, controlFlow);
                tokensToNavigate.add(newToken);
            }
            
            // this is needed, as the this-token would be left on the node that triggers the split.
            instance.removeToken(this);
        }
        return tokensToNavigate;
    }

    @Override
    public boolean isSuspandable() {

        return true;
    }

    /**
     * Resumes the execution of the activity and completes it.
     * 
     * @param resumeObject
     *            - an object that is passed from class that resumes the Token
     * @throws NoValidPathException
     *             thrown if there is no valid path to be executed
     */
    private void resumeAndCompleteExecution(Object resumeObject)
    throws NoValidPathException {
        changeActivityState(ActivityState.ACTIVE);
        currentNode.getActivityBehaviour().resume(this, resumeObject);

        completeExecution();
    }
}
