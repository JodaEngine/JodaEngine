package de.hpi.oryxengine.rest.api;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hpi.oryxengine.ServiceFactory;
import de.hpi.oryxengine.correlation.registration.StartEvent;
import de.hpi.oryxengine.exception.DefinitionNotFoundException;
import de.hpi.oryxengine.navigator.Navigator;
import de.hpi.oryxengine.navigator.NavigatorStatistic;
import de.hpi.oryxengine.process.instance.AbstractProcessInstance;
import de.hpi.oryxengine.process.token.Token;

/**
 * API servlet providing an interface for the navigator. It can be used to start/stop process instances.
 */
@Path("/navigator")
@Produces({ MediaType.APPLICATION_JSON })
public class NavigatorWebService implements Navigator {

    private static final String NOT_ACCESSIBLE_VIA_WEBSERVICE = "This method is not accessible via web service.";
    
    private static final String XML_START = "<?xml";
    private static final String XML_END = "</definitions>";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Navigator navigatorService;

    /**
     * Default Constructor.
     */
    public NavigatorWebService() {

        logger.info("NavigatorWebService is initializing");
        navigatorService = ServiceFactory.getNavigatorService();
    }

    @Path("/status/statistic")
    @GET
    @Override
    public NavigatorStatistic getStatistics() {

        return this.navigatorService.getStatistics();
    }

    /**
     * Starts a process instance according to the given process definition ID.
     * 
     * TODO version 2.0.1.GA of RESTeasy does not support UUID, version 2.2-beta-1 works, but has a problem with Jackson
     * Remove this method once RESTeasy 2.2 is stable or beta-2? fixed this problem.
     * 
     * @param definitionID
     *            the id of the process definition to be instantiated and started
     * @throws DefinitionNotFoundException
     *             thrown if the process definition is not found
     * @return returns the created instance
     */
    @Path("/processdefinitions/{definition-id}/instances")
    @POST
    public AbstractProcessInstance startProcessInstance(@PathParam("definition-id") String definitionID)
    throws DefinitionNotFoundException {

        return startProcessInstance(UUID.fromString(definitionID));
    }
    
    /**
     * Deploy a definition from an uploaded xml.
     *
     * @param file the xml representation
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Path("/processdefinitions")
    @POST
    public void deployDefinitionFromXML(String file) throws IOException {
        logger.debug(file.toString());
        int xmlStart = file.indexOf(XML_START);
        // it is the end so we need to add back the length of the found element
        int xmlEnd = file.indexOf(XML_END) + XML_END.length();
        String xmlContent = file.substring(xmlStart, xmlEnd);
        logger.debug(xmlContent);
        

    }

    /**
     * <code> Here this method needs to be implemented due to implementation of the interface's methods.</code>
     * <br/>
     * {@inheritDoc}
     */
    @Override
    public AbstractProcessInstance startProcessInstance(@PathParam("definition-id") UUID definitionID)
    throws DefinitionNotFoundException {

        return navigatorService.startProcessInstance(definitionID);
    }

    @Path("/status/is-idle")
    @GET
    @Override
    public boolean isIdle() {

        return this.navigatorService.isIdle();
    }

    @Path("/status/running-instances")
    @GET
    @Override
    public List<AbstractProcessInstance> getRunningInstances() {

        return this.navigatorService.getRunningInstances();
    }

    @Path("/status/finished-instances")
    @GET
    @Override
    public List<AbstractProcessInstance> getEndedInstances() {

        return this.navigatorService.getEndedInstances();
    }

    @Override
    public void start() {

        throw new UnsupportedOperationException(NOT_ACCESSIBLE_VIA_WEBSERVICE);
    }

    @Override
    public void stop() {

        throw new UnsupportedOperationException(NOT_ACCESSIBLE_VIA_WEBSERVICE);
    }

    @Override
    public void signalEndedProcessInstance(AbstractProcessInstance instance) {

        throw new UnsupportedOperationException(NOT_ACCESSIBLE_VIA_WEBSERVICE);
    }

    @Override
    public AbstractProcessInstance startProcessInstance(UUID processID, StartEvent event)
    throws DefinitionNotFoundException {

        throw new UnsupportedOperationException(NOT_ACCESSIBLE_VIA_WEBSERVICE);
    }

    @Override
    public void addThread() {

        throw new UnsupportedOperationException(NOT_ACCESSIBLE_VIA_WEBSERVICE);
    }

    @Override
    public void addWorkToken(Token t) {

        throw new UnsupportedOperationException(NOT_ACCESSIBLE_VIA_WEBSERVICE);
    }

    @Override
    public void addSuspendToken(Token t) {

        throw new UnsupportedOperationException(NOT_ACCESSIBLE_VIA_WEBSERVICE);
    }

    @Override
    public void removeSuspendToken(Token t) {

        throw new UnsupportedOperationException(NOT_ACCESSIBLE_VIA_WEBSERVICE);
    }
}
