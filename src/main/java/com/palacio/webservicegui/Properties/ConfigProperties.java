package com.palacio.webservicegui.Properties;

import com.palacio.webservicegui.LogConfig.LogConfig;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigProperties {

    private static final Logger logger = Logger.getLogger(LogConfig.class.getName());

    private static final String PROPERTIES_FILE = "C:\\Users\\josue\\OneDrive\\Documents\\NetBeansProjects\\WebServiceGUI\\src\\main\\java\\com\\palacio\\webservicegui\\config.properties";
    //private static final String PROPERTIES_FILE = ".\\config.properties";

    public static String USER;
    public static String PASSWORD;
    public static String IP_DESARROLLO;
    public static String IP_PRODUCCION;
    public static String URL_TOKEN_DESARROLLO;
    public static String URL_TOKEN_PRODUCCION;
    public static String URL_CARD_DESARROLLO;
    public static String URL_CARD_PRODUCCION;
    public static String URL_DESARROLLO;
    public static String URL_PRODUCCION;

    public static void loadProperties() {
        logger.log(Level.INFO, "Cargando valores CONFIG.PROPERTIES...");
        Properties properties = new Properties();
        try(InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(input);

            USER = properties.getProperty("USER");
            PASSWORD = properties.getProperty("PASSWORD");
            URL_TOKEN_DESARROLLO = properties.getProperty("URL_TOKEN_DESARROLLO");
            URL_TOKEN_PRODUCCION = properties.getProperty("URL_TOKEN_PRODUCCION");
            URL_CARD_DESARROLLO = properties.getProperty("URL_CARD_DESARROLLO");
            URL_CARD_PRODUCCION = properties.getProperty("URL_CARD_PRODUCCION");
            URL_DESARROLLO = properties.getProperty("URL_DESARROLLO");
            URL_PRODUCCION = properties.getProperty("URL_PRODUCCION");

            IP_DESARROLLO = extractIPAddress(URL_TOKEN_DESARROLLO);
            IP_PRODUCCION = extractIPAddress(URL_TOKEN_PRODUCCION);

            logger.log(Level.INFO, "Valores CONFIG.PROPERTIES cargados correctamente");
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Error al cargar valores CONFIG.PROPERTIES. {0}", e);
        }
    }

    private static String extractIPAddress(String input) {
        String ipAddress = "";
        String regex = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            ipAddress = matcher.group();
        }

        return ipAddress;
    }
}
