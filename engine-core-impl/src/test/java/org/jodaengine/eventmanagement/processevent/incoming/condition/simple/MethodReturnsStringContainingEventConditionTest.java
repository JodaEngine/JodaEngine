package org.jodaengine.eventmanagement.processevent.incoming.condition.simple;

import org.jodaengine.eventmanagement.adapter.mail.MailAdapterEvent;
import org.jodaengine.eventmanagement.processevent.incoming.condition.complex.AbstractMultipleEventCondition;
import org.jodaengine.eventmanagement.processevent.incoming.condition.complex.OrEventCondition;
import org.jodaengine.eventmanagement.subscription.condition.EventCondition;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Testing the class MethodReturnsStringContainingEventCondition,
 * it should return true as long as the String returned by the specified
 * method returns the specified string.
 */
public class MethodReturnsStringContainingEventConditionTest {

    private static final String ID = "ID";
    private MailAdapterEvent event;
    
    /**
     * Sets the up.
     */
    @BeforeClass
    public void setUp() {
        event = Mockito.mock(MailAdapterEvent.class);
        Mockito.when(event.getMessageSubject()).thenReturn("Re: " + ID);
        Mockito.when(event.getMessageContent()).thenReturn("LoremIpsum blablablabla LoremIpsum");
    }
    
    /**
     * Test re mail true.
     */
    @Test
    public void testReMailTrue() {
        EventCondition condition = new MethodReturnsStringContainingEventCondition(
            MailAdapterEvent.class, 
            "getMessageSubject", 
            ID);
        Assert.assertTrue(condition.evaluate(event));
    }
    
    /**
     * Test re mail false.
     */
    @Test
    public void testReMailFalse() {
        EventCondition condition = new MethodReturnsStringContainingEventCondition(
            MailAdapterEvent.class, 
            "getMessageSubject", 
            "somethingeElse");
        Assert.assertFalse(condition.evaluate(event));
    }
    
    /**
     * Uses an orCondition to check both the message content and subject.
     * Subject contains the ID so it should be true.
     */
    @Test
    public void testWithOrConditionSubjectTrue() {
        EventCondition condition1 = new MethodReturnsStringContainingEventCondition(
            MailAdapterEvent.class, 
            "getMessageSubject", 
            ID);
        EventCondition condition2 = new MethodReturnsStringContainingEventCondition(
            MailAdapterEvent.class, 
            "getMessageContent", 
            ID);
        AbstractMultipleEventCondition orCondition = new OrEventCondition();
        orCondition.addEventCondition(condition1).addEventCondition(condition2);
        Assert.assertTrue(orCondition.evaluate(event));
    }
}
