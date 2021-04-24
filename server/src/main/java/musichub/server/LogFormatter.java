package musichub.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class LogFormatter extends Formatter {
    /**
     * Setup logger for MusicHub
     * @param fileHandler log output file; does nothing if null
     */
    public static void prepareLogger(String fileHandler) throws IOException {
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // suppress the logging output to the console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            handlers[0].setFormatter(new LogFormatter());
        }

        logger.setLevel(Level.INFO);

        if (fileHandler != null) {
            FileHandler fileTxt = new FileHandler(fileHandler);
            fileTxt.setFormatter(new LogFormatter());

            logger.addHandler(fileTxt);
        }
    }

    @Override
    public String format(LogRecord rec) {
        return "[" + getDate(rec.getMillis()) + "][" + rec.getLevel() + "] " + formatMessage(rec) + "\n";
    }

    private String getDate(long ms) {
        SimpleDateFormat date_format = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
        return date_format.format(new Date(ms));
    }
}
