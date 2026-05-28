package com.campus.complaint;

import java.io.UnsupportedEncodingException;
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
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.connectiontimeout", "5000");  // 5 sec connect timeout
        props.put("mail.smtp.timeout", "5000");            // 5 sec read timeout
        props.put("mail.smtp.writetimeout", "5000");       // 5 sec write timeout

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

            // Set From: header with display name
            try {
                message.setFrom(new InternetAddress(SENDER_EMAIL, "CampusCare System"));
            } catch (UnsupportedEncodingException e) {
                message.setFrom(new InternetAddress(SENDER_EMAIL));
            }

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
