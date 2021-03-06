package org.jodaengine.eventmanagement.adapter.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jodaengine.eventmanagement.adapter.AbstractEventAdapter;
import org.jodaengine.eventmanagement.adapter.AddressableMessage;
import org.jodaengine.eventmanagement.adapter.AddressableMessageWithSubject;
import org.jodaengine.eventmanagement.adapter.outgoing.OutgoingMessagingAdapter;
import org.jodaengine.exception.JodaEngineRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Adapter for sending emails via the SMTP protocol.
 */
public class OutgoingMailAdapter extends AbstractEventAdapter<OutgoingMailAdapterConfiguration> 
    implements OutgoingMessagingAdapter {

    // TODO @EVENTTEAM Usage of USERNAME and PASSWORD missing
    public final static String DEFAULT_SUBJECT = "JodaMail, you have!";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * Create a new outgoing MailAdapter from its configuration.
     *
     * @param configuration the configuration
     */
    public OutgoingMailAdapter(OutgoingMailAdapterConfiguration configuration) {
        super(configuration);
    }

    
    /**
     * Send a message with a subject.
     *
     * @param message the message
     */
    public void sendMessage(AddressableMessage message) {
        sendSingleRecipientEmail(message.getAddress(), DEFAULT_SUBJECT, message.getContent());
    }

    
    /**
     * Send message.
     *
     * @param message the message
     */
    public void sendMessage(AddressableMessageWithSubject message) {

        this.sendSingleRecipientEmail(message.getAddress(), message.getSubject(), message.getContent());
        
    }
    
    /**
     * Send an email to a single recipient email.
     *
     * @param recipient the recipient
     * @param subject the subject
     * @param content the content
     */
    public void sendSingleRecipientEmail(String recipient, String subject, String content) {
        String[] receipents = {recipient};
        this.sendEmail(receipents, subject, content);
        
    }
    
    
    /**
     * Send an email.
     *
     * @param recipients the recipients of the email
     * @param subject the subject of the email
     * @param message the message to be send as an email
     */
    private void sendEmail(String[] recipients, String subject, String message)
    {
        try {
             //Set the SMTP address
             Properties props = new Properties();
             props.put("mail.smtp.host", this.configuration.getSmtpServer());
    
            // create some properties and get the default Session
            Session session = Session.getDefaultInstance(props, null);
            
            // at this point you may setDebug if needed.
    
            Message msg = new MimeMessage(session);
    
            // set the from and to address
            InternetAddress addressFrom = new InternetAddress(this.configuration.getEmailAddress());
                msg.setFrom(addressFrom);
            
            // An email may have multiple recipients
            InternetAddress[] addressTo = new InternetAddress[recipients.length]; 
            for (int i = 0; i < recipients.length; i++) {
                addressTo[i] = new InternetAddress(recipients[i]);
            }
            msg.setRecipients(Message.RecipientType.TO, addressTo);
           
            // Setting the Subject and Content Type
            msg.setSubject(subject);
            msg.setContent(message, "text/plain");
            Transport.send(msg);
        } catch (MessagingException e) {
            String errorMessage = "A messaging exception occured! Something is wrong with the mail you tried to send!";
            logger.error(errorMessage, e);
            throw new JodaEngineRuntimeException(errorMessage, e);
        }

    }


    @Override
    public void sendMessage(org.jodaengine.eventmanagement.adapter.Message message) {

        throw new JodaEngineRuntimeException(
            "Please supply at least an adress when trying to send an email, it can't be send otherwise!"
            + "Try using something that implements AddressableMessage or a subclass of it.");
        
    }
}
