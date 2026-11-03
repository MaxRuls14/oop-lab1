import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Logger {
    // static: shared by all loggers (they belong to the class, not to an object)
    private static LogLevel level = LogLevel.INFO;
    private static String format = "[%t] [%l] %n: %m";

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final Random random = new Random();

    // random messages for %r: one row per level, in the same order as LogLevel (TRACE, INFO, WARN, ERROR)
    private static final String[][] RANDOM_MESSAGES = {
        {"Stepping through the input", "Still tracing", "Checking next character"},
        {"Operation completed", "Everything looks good", "Here is your result"},
        {"Something looks odd", "Proceed with caution", "Unexpected, but not fatal"},
        {"Operation failed", "Something went wrong", "Invalid input detected"}
    };

    // instance: every logger has its own name
    private final String name;

    public Logger(String name) {
        this.name = name;
    }

    public static void setLevel(LogLevel newLevel) {
        level = newLevel;
    }

    public static void setFormat(String newFormat) {
        format = newFormat;
    }

    public void log(LogLevel msgLevel, String message) {
        // ignore messages that are less severe than the configured level
        if (msgLevel.ordinal() < level.ordinal()) {
            return;
        }
        System.out.println(buildText(msgLevel, message));
    }

    public void trace(String message) {
        log(LogLevel.TRACE, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    // goes through the format once and replaces every placeholder
    // (one pass, so a "%n" written inside the message is not replaced again)
    private String buildText(LogLevel msgLevel, String message) {
        String result = "";
        int i = 0;
        while (i < format.length()) {
            char c = format.charAt(i);
            String value = null;
            if (c == '%' && i + 1 < format.length()) {
                value = getValue(format.charAt(i + 1), msgLevel, message);
            }
            if (value != null) {
                result += value;
                i += 2;      // skip the % and the letter
            } else {
                result += c; // normal character, print it unchanged
                i++;
            }
        }
        return result;
    }

    // returns the text for a placeholder letter, or null if the letter is not a placeholder
    private String getValue(char letter, LogLevel msgLevel, String message) {
        if (letter == 't') {
            return LocalTime.now().format(TIME_FORMAT);
        } else if (letter == 'm') {
            return message;
        } else if (letter == 'n') {
            return name;
        } else if (letter == 'l') {
            return msgLevel.name();
        } else if (letter == 'r') {
            String[] options = RANDOM_MESSAGES[msgLevel.ordinal()];
            return options[random.nextInt(options.length)];
        }
        return null;
    }
}
