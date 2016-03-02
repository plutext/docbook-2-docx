package com.alphasystem.docbook.util;

import com.alphasystem.docbook.ApplicationController;
import org.apache.commons.configuration2.CompositeConfiguration;

/**
 * @author sali
 */
public class ConfigurationUtils {

    private static ConfigurationUtils instance;

    public static synchronized ConfigurationUtils getInstance() {
        if (instance == null) {
            instance = new ConfigurationUtils();
        }
        return instance;
    }

    private final CompositeConfiguration configuration;

    /**
     * Do not let any one instantiate this class.
     */
    private ConfigurationUtils() {
        configuration = ApplicationController.getInstance().getConfiguration();
    }

    public String getExampleCaption() {
        return configuration.getString("example-caption");
    }

    public String getTableOfContentCaption(){
        return configuration.getString("toc-caption");
    }
}
