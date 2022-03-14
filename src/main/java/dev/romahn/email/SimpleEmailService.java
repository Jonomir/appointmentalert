package dev.romahn.email;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

@Singleton
public class SimpleEmailService implements EmailService {

    @Value("${email.host}")
    private String hostName;

    @Value("${email.port:`25`}")
    private Integer port;

    @Value("${email.start.tls.enabled}")
    private boolean startTlsEnabled;

    @Value("${email.username}")
    private String username;

    @Value("${email.password}")
    private String password;

    @Value("${email.from}")
    private String from;

    @Override
    public void sendEmail(String subject, String message, String... to) throws Exception {
        Email email = new SimpleEmail();

        email.setHostName(hostName);
        email.setSmtpPort(port);
        email.setStartTLSEnabled(startTlsEnabled);
        email.setAuthentication(username, password);
        email.setFrom(from);

        email.setSubject(subject);
        email.setMsg(message);
        for (String address : to) {
            email.addTo(address);
        }

        email.send();
    }
}
