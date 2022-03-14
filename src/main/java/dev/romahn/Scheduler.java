package dev.romahn;

import dev.romahn.email.EmailService;
import dev.romahn.persistence.PersistenceProvider;
import dev.romahn.scraper.AppointmentScraper;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.convert.format.Format;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Singleton
public class Scheduler {

    private static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);

    @Inject private AppointmentScraper appointmentScraper;
    @Inject private PersistenceProvider<LocalDate, Integer> persistenceProvider;
    @Inject private EmailService emailService;

    @Value("${time.window.start}") @Format("yyyy-MM-dd")
    private LocalDate startDate;

    @Value("${time.window.end}") @Format("yyyy-MM-dd")
    private LocalDate endDate;

    @Value("${email.to}")
    private String emailTo;

    @Scheduled(initialDelay = "1m", fixedDelay = "${loop.time:2m}")
    public void schedule() throws Exception {
        LOG.info("Looking for new appointments");

        StringBuilder emailText = new StringBuilder();
        getNewAppointments().forEach((date, slots) -> {
            LOG.info("Found new appointment on {}, there are {} slots available", date, slots);
            emailText.append("Date: ").append(date).append(", Slots: ").append(slots).append(System.lineSeparator());
        });

        if (!emailText.isEmpty()) {
            emailService.sendEmail("New Appointments available", emailText.toString(), emailTo.split("\\|"));
        }

        LOG.info("Done");
    }

    private Map<LocalDate, Integer> getNewAppointments() throws Exception {
        Map<LocalDate, Integer> availableAppointments = appointmentScraper.getAvailableAppointments();

        Map<LocalDate, Integer> currentAppointmentsInRange = availableAppointments.entrySet()
                .stream().filter(map -> map.getKey().isAfter(startDate.minusDays(1)) && map.getKey().isBefore(endDate.plusDays(1)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e, r) -> r, TreeMap::new));

        Map<LocalDate, Integer> previousAppointmentsInRange = persistenceProvider.get();
        persistenceProvider.set(currentAppointmentsInRange);
        previousAppointmentsInRange.keySet().forEach(currentAppointmentsInRange::remove);
        return currentAppointmentsInRange;
    }

}
