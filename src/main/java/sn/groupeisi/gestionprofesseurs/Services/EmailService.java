package sn.groupeisi.gestionprofesseurs.Services;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    public static void sendEmail(String to, String subject, String body) {
        // Configuration du serveur SMTP
        String host = "smtp.example.com"; // Remplacez par votre serveur SMTP
        String from = "your-email@example.com"; // Remplacez par votre adresse e-mail
        final String username = "your-email@example.com"; // Votre adresse e-mail
        final String password = "your-email-password"; // Votre mot de passe

        // Configuration des propriétés du serveur SMTP
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");

        // Créer une session avec l'authentification SMTP
        Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Créer un objet MimeMessage
            MimeMessage message = new MimeMessage(session);

            // Définir les informations d'expéditeur et de destinataire
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Définir le sujet et le corps du message
            message.setSubject(subject);
            message.setText(body);

            // Envoyer l'e-mail
            Transport.send(message);
            System.out.println("E-mail envoyé avec succès à " + to);

        } catch (MessagingException mex) {
            System.err.println("Erreur d'envoi d'email : " + mex.getMessage());
            mex.printStackTrace();
        }
    }
}
