package org.apache.commons.mail;

import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.DriverPropertyInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;


public class EmailTest {
	
	public static final String[] TEST_EMAILS = {
			"az@za.com", "a.b.c@c.org", "adsadafhjfhsakfafsf@dasdafjkhasfasf.co.uk" 
	};
	
	EmailConcrete email;
	
	@Before
	public void setupEmailTest() {
		email = new EmailConcrete();
	}
	
	@After
	public void tearDownEmailTest() throws EmailException {
		
	}

	@Test
	public void testAddBcc() throws EmailException {
		email.addBcc(TEST_EMAILS);
		
		assertEquals(email.bccList.size(), 3);
	}
	
	@Test(expected = EmailException.class)
	public void testAddBccInvalid() throws EmailException {
		String[] invalidBccStrings = {};
		
		email.addBcc(invalidBccStrings);
	}
	
	@Test
	public void testAddBccSingle() throws EmailException {
		String emailAddress = "azqz@hotmail.com";
		
		email.addBcc(emailAddress);
		
		assertEquals(email.bccList.get(0).getAddress(), emailAddress);
	}
	
	@Test
	public void testAddCc() throws EmailException {
		email.addCc(TEST_EMAILS);
		
		assertEquals(email.ccList.size(), 3);

	}
	
	@Test(expected = EmailException.class)
	public void testAddCcInvalid() throws EmailException {
		String[] invalidCcStrings = {};
		
		email.addCc(invalidCcStrings);
	}
	
	@Test
	public void testAddCcSingle() throws EmailException {
		String emailAddress = "azqz@hotmail.com";
		
		email.addCc(emailAddress);
		
		assertEquals(email.ccList.get(0).getAddress(), emailAddress);
	}
	
	@Test
	public void testAddHeader() {
		String headerKeyString = "key";
		String headerValString = "VALUE";
		
		email.addHeader(headerKeyString, headerValString);
		
		assertEquals(email.headers.get(headerKeyString), headerValString);
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddHeaderInvalidKey() {
		String headerKeyString = null;
		String headerValString = "VALUE";
		
		// Invalid key "null"
		
		email.addHeader(headerKeyString, headerValString);
	}

	
	@Test(expected = IllegalArgumentException.class)
	public void testAddHeaderInvalidValue() {
		String headerKeyString = "key";
		String headerValString = null;
		
		// Invalid value "null"
		
		email.addHeader(headerKeyString, headerValString);
	}
	
	@Test
	public void testAddReply() throws EmailException {
		String emailString= "azqz@gmail.com";
		String nameString = "azqz";
		
		email.addReplyTo(emailString, nameString);
		
		assertEquals(email.replyList.get(0).getAddress(), emailString);
		assertEquals(email.replyList.get(0).getPersonal(), nameString);	
	}
	
	@Test
	public void testAddReplyNoName() throws EmailException {
		String emailString= "azqz@gmail.com";
		
		email.addReplyTo(emailString);
		
		assertEquals(email.replyList.get(0).getAddress(), emailString);
	}
	

	
	@Test(expected = IllegalStateException.class)
	public void testBuildMimeMessagSimple() throws EmailException, MessagingException {
		String fromString = "azqz@dev.com";
		String toAddressString = "azqz@umich.edu";
		
		Collection<InternetAddress> to = new ArrayList<InternetAddress>();
		
		to.add(new InternetAddress(toAddressString));
				
		email.setMailSession(Session.getInstance(new Properties()));
		
		email.setFrom(fromString);
		email.setTo(to);
		
		email.setSubject("subject");
						
		email.buildMimeMessage();
		
		
		// Make sure the message is not null
		assertNotNull(email.message);
		
		// Make sure the sender and receiver are correct
		assertEquals(email.message.getFrom()[0].toString(), fromString);
		assertEquals(email.message.getAllRecipients()[0].toString(), toAddressString);
		
		// Since we didn't specify a charset, it should be null
		assertNull(email.charset);
		
		// Shouldn't be able to build the message again
		email.buildMimeMessage();
	}
	
	@Test(expected = EmailException.class)
	public void testBuildMimeMessageInvalid() throws EmailException {				
		Session session = Session.getInstance(new Properties());
		
		email.setMailSession(session);
		
		// No "to", or "from"
		
		email.buildMimeMessage();
	}
	
	@Test
	public void testBuildMimeMessageRest() throws EmailException, MessagingException {
		Properties properties = new Properties();

		String fromString = "azqz@dev.com";
		String toAddressString = "azqz@umich.edu";
		
		// With every other property
		
		properties.setProperty(EmailConstants.MAIL_SMTP_FROM, fromString);
		
		Session session = Session.getInstance(properties);
		
		email.setMailSession(session);
		
		
		String header1Key = "key";
		String header1Val = "value";
		
		Collection<InternetAddress> to = new ArrayList<InternetAddress>();
		
		to.add(new InternetAddress(toAddressString));
				
		email.setMailSession(Session.getInstance(new Properties()));
		
		email.setCharset("UTF-8");
		email.setSubject("subject");
		
		email.setFrom(fromString);
		email.setTo(to);
		
		email.addHeader(header1Key, header1Val);
		
		email.addBcc("azqzhotiri@bcc.com");
		email.addCc("azqzhotiri@cc.com");
		email.addReplyTo("azqzhotiri@replyto.com");
		
		email.buildMimeMessage();
		
		// Make sure all the values are set correctly
		
		assertEquals(email.charset, "UTF-8");
		
		assertEquals(email.headers.size(), 1);
		assertEquals(email.message.getRecipients(RecipientType.CC).length, 1);
		assertEquals(email.message.getRecipients(RecipientType.BCC).length,1);
		assertEquals(email.message.getRecipients(RecipientType.TO).length,1);
		assertEquals(email.message.getAllRecipients().length, 3);

		assertNotNull(email.headers.get(header1Key));
		
		
		assertEquals(email.headers.get(header1Key), header1Val);
	}
	

	@Test(expected = EmailException.class)
	public void testBuildMimeMessageWithTextContent() throws EmailException {
		Session session = Session.getInstance(new Properties());
		
		email.setMailSession(session);
		
		// text type
		email.setContent("Hello World", "text/plain");
	
		
		email.setFrom("azqz@hotmail.com");
		
		// can't build since no recipient list
		email.buildMimeMessage();
	}
	
	@Test(expected = EmailException.class)
	public void testBuildMimeMessageWithDifferentContent() throws EmailException {
		Session session = Session.getInstance(new Properties());
		
		email.setMailSession(session);
		
		// text type
		email.setContent("Hello World", "other");
		
		email.setFrom("azqz@hotmail.com");
		
		// can't build since no recipient list
		email.buildMimeMessage();
	}
	
	private void setupBuildMime() throws EmailException, AddressException {
		Session session = Session.getInstance(new Properties());
		
		email.setMailSession(session);
	
		
		email.setFrom("azqz@hotmail.com");
		
		ArrayList<InternetAddress> to = new ArrayList<InternetAddress>();
		to.add(new InternetAddress("azqz@hotmail.com"));
		
		email.setTo(to);
	}
	
	@Test
	public void testBuildMimeMessageWithBodyNoType() throws EmailException, AddressException {
		setupBuildMime();
		
		// text type
		email.setContent(new MimeMultipart());
		
		email.buildMimeMessage();
	}
	
	@Test
	public void testBuildMimeMessageWithBodyWithType() throws EmailException, AddressException {
		setupBuildMime();

		// text type
		email.setContent(new MimeMultipart());
		email.updateContentType("multipart/form");
	
		
		email.buildMimeMessage();
	}
	
	@Test(expected = EmailException.class)
	public void testBuildMimeMessageStoreInvalid() throws EmailException, AddressException {
		setupBuildMime();
		
		email.setPopBeforeSmtp(true, null, null, null);
		
		email.buildMimeMessage();
	}
	
	@Test(expected = EmailException.class)
	public void testBuildMimeMessageStoreValid() throws EmailException, AddressException {
		setupBuildMime();

		email.setPopBeforeSmtp(true, null, null, null);
		
		email.buildMimeMessage();
	}
	
	@Test
	public void testGetHostname() throws AddressException, EmailException {		
		String hostName = "localhost";
		
		email.setHostName(hostName);
		
		assertEquals(email.getHostName(), hostName);
	}
	
	@Test
	public void testGetHostnameFromSession() {
		Properties properties = new Properties();
		properties.setProperty(EmailConstants.MAIL_HOST, "localhost");
		
		Session session = Session.getInstance(properties);
		
		email.setMailSession(session);
		
		assertEquals(email.getHostName(), "localhost");
	}
	
	@Test
	public void testGetHostnameNull() throws EmailException {			
		assertNull(email.getHostName());
	}
	
	@Test
	public void testGetSentDate() {
		Date date = new Date(0);
		
		email.setSentDate(date);
		
		assertEquals(email.getSentDate(), date);
	}
	
	@Test
	public void testGetSocketConnectionTimeout() {
		int timeout = 30;
		
		email.setSocketConnectionTimeout(timeout);
		
		assertEquals(email.getSocketConnectionTimeout(), timeout);
	}
	
	@Test(expected = EmailException.class)
	public void testGetMailSessionNoHostName() throws EmailException {
		email.getMailSession();
	}
	
	@Test
	public void testGetMailSessionWithHostName() throws EmailException {
		email.setHostName("localhost");
		
		email.setAuthenticator(new Authenticator() {
			
		});
		
		email.setBounceAddress("azqzhotiri@hotmail.com");
		
		email.setStartTLSEnabled(true);
		
		email.setSSLOnConnect(true);
		
		email.setSSLCheckServerIdentity(true);
		
		email.getMailSession();
	}
	
}
