package org.jodaengine.factory.resource;

import java.util.List;
import java.util.Random;

import org.jodaengine.RepositoryService;
import org.jodaengine.ServiceFactory;
import org.jodaengine.factory.worklist.CreationPatternFactory;
import org.jodaengine.process.token.Token;
import org.jodaengine.resource.AbstractParticipant;
import org.jodaengine.resource.allocation.CreationPattern;
import org.jodaengine.resource.allocation.PushPattern;
import org.jodaengine.resource.allocation.pattern.push.AllocateSinglePattern;
import org.jodaengine.resource.worklist.AbstractWorklistItem;
import org.jodaengine.resource.worklist.WorklistService;
import org.mockito.Mockito;

/**
 * A little factory for creating {@link AbstractParticipant}s.
 */
public class ParticipantFactory extends ResourceFactory {

    private static final short NUMBER_OF_ITEMS_FOR_BUSY_PARTICIPANT = 100;

    /**
     * Returns a {@link AbstractParticipant} object with the following parameter
     * name: Gerardo Navarro Suarez
     * 
     * @return the {@link AbstractParticipant} Gerardo
     */
    public static AbstractParticipant createGerardo() {

        return createParticipant("Gerardo Navarro Suarez");
    }

    /**
     * Returns a {@link AbstractParticipant} object with the the following parameter
     * name: Jannik Streek
     * 
     * @return the {@link AbstractParticipant} Jannik
     */
    public static AbstractParticipant createJannik() {

        return createParticipant("Jannik Streek");
    }

    /**
     * Returns a {@link AbstractParticipant} object with the the following parameter
     * name: Tobias Pfeiffer
     * 
     * @return the {@link Participant} Tobi
     */
    public static AbstractParticipant createTobi() {

        return createParticipant("Tobias Pfeiffer");
    }

    /**
     * Returns a {@link AbstractParticipant} object with the the following parameter
     * name: Tobias Metzke
     * 
     * @return the {@link Participant} Tobi2
     */
    public static AbstractParticipant createTobi2() {

        return createParticipant("Tobias Metzke");
    }

    /**
     * Returns a {@link AbstractParticipant} object with the the following parameter
     * id: willi.joda
     * name: Willi Joda
     * 
     * @return the {@link Participant} BusyWilli
     */
    public static AbstractParticipant createBusyWilli() {

        WorklistService worklistService = ServiceFactory.getWorklistService();
        Random rand = new Random();

        AbstractParticipant willi = createParticipant("Willi Joda");

        willi.setProperty("some-property", "some-value");
        willi.setProperty("another-property", "another-value");

        CreationPattern pattern = null;
        for (int i = 0; i < NUMBER_OF_ITEMS_FOR_BUSY_PARTICIPANT; i++) {
            pattern = CreationPatternFactory.createDirectDistributionPattern(willi);
            AbstractWorklistItem item = pattern.createWorklistItem(Mockito.mock(Token.class),
                Mockito.mock(RepositoryService.class));
            PushPattern pushPattern = new AllocateSinglePattern();
            pushPattern.distributeItem(ServiceFactory.getInteralWorklistService(), item);
            // taskDistributionService.distribute(pattern, Mockito.mock(Token.class));
        }

        List<AbstractWorklistItem> items = worklistService.getWorklistItems(willi);
        for (AbstractWorklistItem item : items) {
            if (rand.nextBoolean()) {
                worklistService.claimWorklistItemBy(item, willi);
            }
        }

        return willi;
    }

}
