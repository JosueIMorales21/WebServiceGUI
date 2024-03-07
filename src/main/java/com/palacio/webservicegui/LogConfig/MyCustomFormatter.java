package com.palacio.webservicegui.LogConfig;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class MyCustomFormatter extends Formatter {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String customInfoLevelName;

    public MyCustomFormatter(String customInfoLevelName) {
        this.customInfoLevelName = customInfoLevelName;
    }

    public MyCustomFormatter() {
        this.customInfoLevelName = "INFORMACIÓN";
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();

        if(record.getMessage().equals("----------------------------------------------------------------------------------------------------")) {
            return "----------------------------------------------------------------------------------------------------";
        }

        builder.append("[").append(dateFormat.format(new Date())).append("]");

        builder.append("[").append(getCustomLevelName(record.getLevel())).append("]");

        builder.append(" ").append(formatMessage(record));

        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            builder.append(System.lineSeparator()).append(sw.toString());
        }

        builder.append(System.lineSeparator());

        return builder.toString();
    }

    private String getCustomLevelName(Level level) {
        if (level == Level.SEVERE) {
            return "ERROR";
        } else if (level == Level.INFO) {
            return customInfoLevelName;
        } else {
            return level.getName();
        }
    }
}
