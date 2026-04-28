package com.campus.complaint;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

    // IMPORTANT: Replace with valid email credentials before using.
    private static final String SENDER_EMAIL = "suhasgowda636227@gmail.com";
    private static final String SENDER_PASSWORD = "irmqcidytsszyodq";

    public static void sendEmail(String recipientEmail, String subject, String body) {
        // Setup mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // Using Gmail SMTP as an example
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Get the Session object
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });
        session.setDebug(true);

        try {
            // Create a default MimeMessage object
            Message message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress(SENDER_EMAIL));

            // Set To: header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            // Set Subject: header field
            message.setSubject(subject);

            // Set the actual message
            message.setContent(body, "text/html; charset=utf-8");

            // Send message
            Transport.send(message);

            System.out.println("Email sent successfully to: " + recipientEmail);

        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + recipientEmail);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
