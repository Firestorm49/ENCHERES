package fr.eni.encheres.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final String LOG_FOLDER = "EVENT";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void log(String logFileName, String message) {
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = DATE_TIME_FORMATTER.format(now);
        String logMessage = "[" + formattedDateTime + "] " + message;

        File logFolder = new File(LOG_FOLDER);
        if (!logFolder.exists()) {
            logFolder.mkdirs();
        }

        try (FileWriter writer = new FileWriter(new File(LOG_FOLDER + File.separator + logFileName), true)) {
            writer.write(logMessage + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}