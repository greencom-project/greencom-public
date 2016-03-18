package eu.greencom.mgmbroker.manager.impl;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class ReportMailSender {

	private static final Logger LOG = Logger.getLogger(ReportMailSender.class);
	private String user = null;
	private String password = null;
	private Properties props = new Properties();

	public ReportMailSender(String smtpServer, String smtpServerPort, String user, String password) {
		super();
		this.user = user;
		this.password = password;		
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.port", smtpServerPort);
	}

	public void sendHTMLEmail(String toAddress, String subject, String htmlContent) {
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(user));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
			message.setSubject(subject);
			message.setContent(htmlContent, "text/html");
			Transport.send(message);
		} catch (Exception e) {
			LOG.error("An error occuredd while sending the email: " + e);
		}
	}

}
