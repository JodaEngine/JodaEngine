package de.hpi.oryxengine.resource.allocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.hpi.oryxengine.allocation.AllocationStrategies;
import de.hpi.oryxengine.allocation.Form;
import de.hpi.oryxengine.allocation.Pattern;
import de.hpi.oryxengine.allocation.Task;
import de.hpi.oryxengine.resource.AbstractResource;

/**
 * This class helps to build {@link Task Tasks}.
 */
public class TaskBuilderImpl implements CreationPatternBuilder {

    private String taskSubject;
    private String taskDescription;
    private Form taskForm;
    private Pattern taskPushPattern, taskPullPattern;
    private List<AbstractResource<?>> abstractResources;

    public TaskBuilderImpl() {
        
        this.abstractResources = new ArrayList<AbstractResource<?>>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreationPatternBuilder setItemSubject(String taskSubject) {

        this.taskSubject = taskSubject;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreationPatternBuilder setItemDescription(String taskDescription) {

        this.taskDescription = taskDescription;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreationPatternBuilder setItemForm(Form taskForm) {

        this.taskForm = taskForm;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreationPatternBuilder setTaskPushPattern(Pattern taskPushPattern) {

        this.taskPushPattern = taskPushPattern;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreationPatternBuilder addResourceAssignedToItem(AbstractResource<?> resourceAssignedToTask) {

        this.abstractResources.add(resourceAssignedToTask);
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Task buildTask() {
        
        AllocationStrategies allocationStrategies = new AllocationStrategiesImpl(taskPushPattern, taskPullPattern, null);
        HashSet<AbstractResource<?>> assignedResources = new HashSet<AbstractResource<?>>(abstractResources);
        Task resultTask = new TaskImpl(taskSubject, taskDescription, taskForm, allocationStrategies, assignedResources);
        
        return resultTask;
    }
    
}
