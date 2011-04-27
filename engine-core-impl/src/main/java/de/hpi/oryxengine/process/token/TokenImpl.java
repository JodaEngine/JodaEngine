package de.hpi.oryxengine.process.token;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import de.hpi.oryxengine.activity.Activity;
import de.hpi.oryxengine.activity.ActivityState;
import de.hpi.oryxengine.exception.DalmatinaException;
import de.hpi.oryxengine.exception.NoValidPathException;
import de.hpi.oryxengine.navigator.Navigator;
import de.hpi.oryxengine.plugin.AbstractPluggable;
import de.hpi.oryxengine.plugin.activity.AbstractTokenPlugin;
import de.hpi.oryxengine.plugin.activity.ActivityLifecycleChangeEvent;
import de.hpi.oryxengine.process.instance.AbstractProcessInstance;
import de.hpi.oryxengine.process.instance.ProcessInstanceImpl;
import de.hpi.oryxengine.process.structure.Node;
import de.hpi.oryxengine.process.structure.Transition;

/**
 * The implementation of a process token.
 */
public class TokenImpl extends AbstractPluggable<AbstractTokenPlugin> implements Token {

    private UUID id;

    private Node currentNode;

    private ActivityState currentActivityState = null;

    private AbstractProcessInstance instance;

    private Transition lastTakenTransition;

    private Navigator navigator;

    private List<Token> lazySuspendedProcessingTokens;

    private Activity currentActivity;
    
    private List<AbstractTokenPlugin> plugins;

    /**
     * Instantiates a new token impl.
     * 
     * @param startNode
     *            the start node
     * @param instance
     *            the instance
     */
    public TokenImpl(Node startNode, AbstractProcessInstance instance) {

        this(startNode, instance, null);
    }

    /**
     * Instantiates a new process token impl.
     * 
     * @param startNode
     *            the start node
     * @param instance
     *            the instance
     * @param navigator
     *            the navigator
     */
    public TokenImpl(Node startNode, AbstractProcessInstance instance, Navigator navigator) {

        this.currentNode = startNode;
        this.instance = instance;
        this.navigator = navigator;
        this.id = UUID.randomUUID();
        changeActivityState(ActivityState.INIT);
        this.currentActivity = null;
        this.plugins = new ArrayList<AbstractTokenPlugin>();
    }

    /**
     * Instantiates a new token impl.
     * 
     * @param startNode
     *            the start node
     */
    public TokenImpl(Node startNode) {

        this(startNode, new ProcessInstanceImpl(null), null);
    }

    /**
     * Gets the current node. So the position where the execution of the Processtoken is at.
     * 
     * @return the current node
     * @see de.hpi.oryxengine.process.token.Token#getCurrentNode()
     */
    @Override
    public Node getCurrentNode() {

        return currentNode;
    }

    /**
     * Sets the current node.
     * 
     * @param node
     *            the new current node {@inheritDoc}
     */
    @Override
    public void setCurrentNode(Node node) {

        currentNode = node;
    }

    @Override
    public UUID getID() {

        return id;
    }

    /**
     * Instantiates current node's activity class to be able to execute it.
     * 
     * @return the activity
     */
    private Activity instantiateCurrentActivityClass() {

        Activity activity = null;

        // TODO should we catch these exceptions here, or should we propagate it to a higher level?
        try {
            activity = currentNode.getActivityBlueprint().instantiate();

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return activity;
    }

    @Override
    public void executeStep()
    throws DalmatinaException {

        if (instance.isCancelled()) {
            // the following statement was already called, when instance.cancel() was called. Nevertheless, a token
            // currently in execution might have created new tokens during split that were added to the instance.
            instance.getAssignedTokens().clear();
            return;
        }
        
        lazySuspendedProcessingTokens = getCurrentNode().getIncomingBehaviour().join(this);
        changeActivityState(ActivityState.ACTIVE);

        currentActivity = instantiateCurrentActivityClass();
        currentActivity.execute(this);
        // Aborting the further execution of the process by the token, because it was suspended
        if (this.currentActivityState == ActivityState.SUSPENDED) {
            return;
        }
        completeExecution();
    }

    /**
     * Navigate to.
     * 
     * @param transitionList
     *            the node list
     * @return the list
     */
    @Override
    public List<Token> navigateTo(List<Transition> transitionList) {

        List<Token> tokensToNavigate = new ArrayList<Token>();
        if (transitionList.size() == 1) {
            Transition transition = transitionList.get(0);
            Node node = transition.getDestination();
            this.setCurrentNode(node);
            this.lastTakenTransition = transition;
            changeActivityState(ActivityState.INIT);
            tokensToNavigate.add(this);
        } else {
            for (Transition transition : transitionList) {
                Node node = transition.getDestination();
                Token newToken = createNewToken(node);
                newToken.setLastTakenTransition(transition);
                tokensToNavigate.add(newToken);
            }

            // this is needed, as the this-token would be left on the node that triggers the split.
            instance.removeToken(this);
        }
        return tokensToNavigate;

    }

    /**
     * Creates a new token in the same context.
     * 
     * @param node
     *            the node
     * @return the token {@inheritDoc}
     */
    @Override
    public Token createNewToken(Node node) {

        Token newToken = instance.createToken(node, navigator);
        
        return newToken;
    }

    @Override
    public boolean joinable() {

        return this.instance.getContext().allIncomingTransitionsSignaled(this.currentNode);
    }

    @Override
    public Token performJoin() {

        TokenImpl token = new TokenImpl(currentNode, instance, navigator);
        // give all of this token's observers to the newly created ones.
        for (AbstractTokenPlugin plugin : plugins) {
            token.registerPlugin(plugin);
        }
        
        instance.getContext().removeIncomingTransitions(currentNode);
        return token;
    }

    @Override
    public AbstractProcessInstance getInstance() {

        return instance;
    }

    @Override
    public Transition getLastTakenTransition() {

        return lastTakenTransition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastTakenTransition(Transition t) {

        this.lastTakenTransition = t;
    }

    @Override
    public void suspend() {

        changeActivityState(ActivityState.SUSPENDED);
        navigator.addSuspendToken(this);
    }

    @Override
    public void resume()
    throws DalmatinaException {

        navigator.removeSuspendToken(this);
        completeExecution();
    }

    /**
     * Completes the execution of the activity.
     * @throws NoValidPathException
     *      thrown if there is no valid path to be executed
     */
    private void completeExecution()
    throws NoValidPathException {

        changeActivityState(ActivityState.COMPLETED);
        currentActivity = null;
        
        List<Token> splittedTokens = getCurrentNode().getOutgoingBehaviour().split(getLazySuspendedProcessingToken());

        for (Token token : splittedTokens) {
            navigator.addWorkToken(token);
        }

        lazySuspendedProcessingTokens = null;
        
    }

    /**
     * Gets the lazy suspended processing token.
     * 
     * @return the lazy suspended processing token
     */
    private List<Token> getLazySuspendedProcessingToken() {

        if (lazySuspendedProcessingTokens == null) {
            lazySuspendedProcessingTokens = new ArrayList<Token>();
        }

        return lazySuspendedProcessingTokens;
    }

    @Override
    public Navigator getNavigator() {

        return this.navigator;
    }

    @Override
    public void cancelExecution() {

        if (this.currentActivityState == ActivityState.ACTIVE || this.currentActivityState == ActivityState.SUSPENDED) {
            currentActivity.cancel();
        }

    }

    @Override
    public ActivityState getCurrentActivityState() {

        return currentActivityState;
    }

    @Override
    public Activity getCurrentActivity() {

        return this.currentActivity;
    }
    
    @Override
    public void registerPlugin(@Nonnull AbstractTokenPlugin plugin) {
        this.plugins.add(plugin);
        addObserver(plugin);
    }

    /**
     * Changes the state of the activity that the token currently points to.
     *
     * @param newState the new state
     */
    private void changeActivityState(ActivityState newState) {

        final ActivityState prevState = currentActivityState;
        this.currentActivityState = newState;
        setChanged();
        
        // TODO maybe change the ActivityLifecycleChangeEvent, as we provide the currentActivity here, but it might not be instantiated yet.
        notifyObservers(new ActivityLifecycleChangeEvent(currentActivity, prevState, newState, this));
        
    }

}
