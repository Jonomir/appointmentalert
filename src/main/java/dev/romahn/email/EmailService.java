package dev.romahn.email;

public interface EmailService {
    void sendEmail(String subject, String message, String... to) throws Exception;
}
