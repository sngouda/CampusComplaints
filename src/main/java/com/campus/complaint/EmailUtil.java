package com.campus.complaint;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Sends email via Gmail using SSL on port 465.
 * Uses app password stored in environment variables.
 *
 * Required environment variables on Render:
 *   MAIL_USER  — Gmail address  (e.g. suhasgowda636227@gmail.com)
 *   MAIL_PASS  — Gmail App Password (16-char, no spaces)
 */
public class EmailUtil {

    private static final String SENDER_NAME = "CampusCare System";

    public static void sendEmail(String recipientEmail, String subject, String body) {

        String senderEmail = System.getenv("MAIL_USER");
        String senderPass  = System.getenv("MAIL_PASS");

        // Fallback to hardcoded if env vars not set
        if (senderEmail == null || senderEmail.isEmpty()) {
            senderEmail = "suhasgowda636227@gmail.com";
        }
        if (senderPass == null || senderPass.isEmpty()) {
            senderPass = "irmqcidytsszyodq";
        }

        final String finalEmail = senderEmail;
        final String finalPass  = senderPass;

        Properties props = new Properties();

        // Try SSL on port 465 (more likely to be open on Render than STARTTLS 587)
        props.put("mail.smtp.host",            "smtp.gmail.com");
        props.put("mail.smtp.port",            "465");
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.socketFactory.port",   "465");
        props.put("mail.smtp.socketFactory.class",  "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.ssl.enable",      "true");
        props.put("mail.smtp.ssl.protocols",   "TLSv1.2");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout",           "10000");
        props.put("mail.smtp.writetimeout",      "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(finalEmail, finalPass);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(finalEmail, SENDER_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject, "UTF-8");
            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Email sent to: " + recipientEmail);

        } catch (MessagingException e) {
            System.err.println("Email failed (port 465): " + e.getMessage());
            // Try fallback on port 587 STARTTLS
            sendWithStartTLS(recipientEmail, subject, body, finalEmail, finalPass);
        } catch (Exception e) {
            System.err.println("Email error: " + e.getMessage());
        }
    }

    // Fallback method using STARTTLS port 587
    private static void sendWithStartTLS(String recipientEmail, String subject,
                                          String body, String senderEmail, String senderPass) {
        Properties props = new Properties();
        props.put("mail.smtp.host",            "smtp.gmail.com");
        props.put("mail.smtp.port",            "587");
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols",   "TLSv1.2");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout",           "10000");
        props.put("mail.smtp.writetimeout",      "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPass);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail, SENDER_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject, "UTF-8");
            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Email sent via STARTTLS to: " + recipientEmail);

        } catch (Exception e) {
            System.err.println("Email failed on both ports. Final error: " + e.getMessage());
        }
    }
}
