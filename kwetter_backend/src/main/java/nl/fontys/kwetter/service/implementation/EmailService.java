package nl.fontys.kwetter.service.implementation;

import nl.fontys.kwetter.exceptions.LoginException;
import nl.fontys.kwetter.service.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService implements IEmailService {

    private final JavaMailSender javaMailSender;
    @Value("${frontend.host.location}")
    private String frontendHostLocation;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMessage(String to, String subject, String jwtToken, String userUuid) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(urlBuilder(jwtToken, userUuid));

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new LoginException("Failed to send verification message");
        }
    }

    private String urlBuilder(String jwtToken, String userUuid) {
        return frontendHostLocation + "/verify?uuid=" + userUuid + "&token=" + jwtToken;
    }
}
