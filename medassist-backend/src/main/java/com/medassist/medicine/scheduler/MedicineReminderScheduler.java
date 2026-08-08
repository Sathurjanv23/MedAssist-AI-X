package com.medassist.medicine.scheduler;

import com.medassist.medicine.model.Medicine;
import com.medassist.medicine.repository.MedicineRepository;
import com.medassist.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Scheduled task that fires medicine reminders based on each medicine's reminder times.
 * Runs every minute to check if any medicine's reminder time matches current time (Â±1 min).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MedicineReminderScheduler {

    private final MedicineRepository medicineRepository;
    private final UserRepository userRepository;

    /**
     * Runs every minute â€” checks all active medicines with reminder times.
     */
    @Scheduled(cron = "0 * * * * *") // every minute
    public void sendMedicineReminders() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        List<Medicine> medicines = medicineRepository.findAllActiveWithReminders();

        for (Medicine medicine : medicines) {
            if (medicine.getReminderTimes() == null) continue;
            for (LocalTime reminderTime : medicine.getReminderTimes()) {
                LocalTime reminder = reminderTime.withSecond(0).withNano(0);
                if (reminder.equals(now)) {
                    sendReminder(medicine);
                }
            }
        }
    }

    /**
     * Daily check for medicines ending within the next 7 days â€” sends refill reminders.
     */
    @Scheduled(cron = "0 0 9 * * *") // 9 AM daily
    public void sendRefillReminders() {
        LocalDate today = LocalDate.now();
        List<Medicine> endingSoon = medicineRepository.findAllActiveWithReminders();
        endingSoon.stream()
                .filter(m -> m.getEndDate() != null)
                .filter(m -> {
                    long daysLeft = today.until(m.getEndDate()).getDays();
                    return daysLeft <= (m.getRefillReminderDays() != null ? m.getRefillReminderDays() : 7)
                           && daysLeft >= 0;
                })
                .forEach(m -> log.info("Refill reminder for medicine: {} (user: {})",
                        m.getMedicineName(), m.getUserId()));
    }

    private void sendReminder(Medicine medicine) {
        log.info("Medicine reminder: {} | Dosage: {} | User: {}",
                medicine.getMedicineName(), medicine.getDosage(), medicine.getUserId());
        // TODO: Send push notification / WebSocket event
    }
}

