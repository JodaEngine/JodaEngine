package de.hpi.oryxengine.resource.allocation.pattern;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.hpi.oryxengine.allocation.CreationPattern;
import de.hpi.oryxengine.allocation.Form;
import de.hpi.oryxengine.allocation.TaskAllocation;
import de.hpi.oryxengine.node.activity.bpmn.BpmnHumanTaskActivity;
import de.hpi.oryxengine.process.token.Token;
import de.hpi.oryxengine.resource.AbstractResource;
import de.hpi.oryxengine.resource.worklist.AbstractWorklistItem;
import de.hpi.oryxengine.resource.worklist.WorklistItemImpl;
import de.hpi.oryxengine.resource.worklist.WorklistItemState;


/**
 * DirectPushPattern receives a task and converts it to a {@link AbstractWorklistItem}. This
 * {@link AbstractWorklistItem} is allocated
 * directly to a certain resource according to this pattern.
 * 
 * This implementation represents the pattern shown in here:
 * http://www.workflowpatterns.com/patterns/resource/push/wrp14.php
 * 
 */
public class DirectDistributionPattern implements CreationPattern {
    
    private String subject;
    
    private String description;
    
    private Form form;
    
    private AbstractResource<?>[] resourcesToAssignTo;

    public DirectDistributionPattern(String subject,
                                     String description,
                                     Form form,
                                     AbstractResource<?>[] assignedResources) {
        this.subject = subject;
        this.description = description;
        this.form = form;
        this.resourcesToAssignTo = assignedResources;
    }
    
    @Override
    public void createWorklistItems(TaskAllocation worklistService, Token token) {

        Set<AbstractResource<?>> assignedResourcesCopy = new HashSet<AbstractResource<?>>(
            Arrays.asList(resourcesToAssignTo));
        WorklistItemImpl worklistItem = new WorklistItemImpl(subject, description, form, assignedResourcesCopy, token);
        worklistItem.setStatus(WorklistItemState.ALLOCATED);

        // TODO @Thorben-Refactoring do we need new worklist items for every worker?
        worklistService.addWorklistItem(worklistItem, assignedResourcesCopy);

    }

    // @Override
    // public void execute(Task task, Token token, TaskAllocation worklistService) {
    //
    // WorklistItemImpl worklistItem = new WorklistItemImpl(task, token);
    // worklistItem.setStatus(WorklistItemState.ALLOCATED);
    // worklistService.addWorklistItem(worklistItem, task.getAssignedResources());
    // }
}
