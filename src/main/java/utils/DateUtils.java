package utils;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {
    private static final String dateTimeFormat = "dd-MMM-yyyy h:mm:ss a";
    private static final String dateFormat = "dd-MMM-yyyy";
    private static final String timeFormat = "h:mm:ss a";

    // Function to parse datetime string and return LocalDateTime
    public static LocalDateTime extractDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

        try {
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing datetime: " + e.getMessage());
            return null; // Return null if parsing fails
        }
    }

    // Function to parse datetime string and return only LocalTime
    public static LocalTime extractTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

        try {
            // Parse the full LocalDateTime and extract the time
            return LocalDateTime.parse(dateTimeString, formatter).toLocalTime();
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing time: " + e.getMessage());
            return null; // Return null if parsing fails
        }
    }

    // Function to parse datetime string and return only LocalDate
    public static LocalDate extractDate(String dateTimeString) {
        // Define the formatter matching the input string pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

        // Parse the input string into a LocalDateTime
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

        // Extract and return the LocalDate
        return dateTime.toLocalDate();
    }

    public static String formatDate(LocalDate date) {
        // Define the desired output format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(dateFormat);

        // Format the LocalDate
        return date.format(outputFormatter);
    }

    public static String joinDateAsString(LocalDate date, LocalTime time) {
        // Define the desired format for the date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

        // Combine the LocalDate and LocalTime into a LocalDateTime
        return date.atTime(time).format(formatter);
    }

    public static DateTimeFormatter getLocalDateTimeFormatter() {
        return DateTimeFormatter.ofPattern(dateTimeFormat);
    }

    public static DateTimeFormatter getDateFormatter() {
        return DateTimeFormatter.ofPattern(dateFormat);
    }

    public static boolean isUpcoming(String dateTime) {
        LocalDate date = extractDate(dateTime);
        LocalTime time = extractTime(dateTime);

        assert date != null;
        assert time != null;

        return (date.equals(LocalDate.now()) && time.isAfter(LocalTime.now())) || date.isAfter(LocalDate.now()) ;
    }
}
