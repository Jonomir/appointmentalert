package dev.romahn.scraper;

import java.time.LocalDate;
import java.util.Map;

public interface AppointmentScraper {
    Map<LocalDate, Integer> getAvailableAppointments() throws Exception;
}
