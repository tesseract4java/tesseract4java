/*
 * Copyright (C) 2014 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.input;

import eu.digitisation.log.Messages;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Start-up actions: load default and user properties (user-defined values
 * overwrite defaults).
 *
 * @author R.C.C.
 */
public class Settings {

    private static Properties props = new Properties();

    /**
     * Get application directory
     */
    private static File appDir() {
        try {
            URI uri = Messages.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI();
            File dir = new File(uri.getPath()).getParentFile();

            Messages.info("Application folder is " + dir);
            return dir;
        } catch (URISyntaxException ex) {
            Messages.severe(Settings.class.getName() + ": " + ex);
        }
        return null;
    }

    static {
        try {
            InputStream in;
            // Read defaults
            Properties defaults = new Properties();
            in = Settings.class.getResourceAsStream("/defaultProperties.xml");
            if (in != null) {
                defaults.loadFromXML(in);
                in.close();
                props = new Properties(defaults);
            }

            // Add user properties (may overwrite defaults)            
            File file = new File(appDir(), "userProperties.xml");
            if (file.exists()) {
                in = new FileInputStream(file);
                props.loadFromXML(in);
                Messages.info("Read properties from " + file);
                in.close();
            } else {
                in = Settings.class.getResourceAsStream("/userProperties.xml");
                if (in != null) {
                    defaults.loadFromXML(in);
                    Messages.info("Read properties from " + file);
                    in.close();
                    props = new Properties(defaults);
                } else {
                    Messages.info("No properties were defined by user");
                }
            }
        } catch (IOException ex) {
            Messages.severe(Settings.class.getName() + ": " + ex);
        }
    }

    /**
     * @return the properties defined at startup (user-defined overwrite
     * defaults).
     */
    public static Properties properties() {
        return props;
    }

    /**
     *
     * @param key a property name
     * @return the property with the specified key as defined by the user, and
     * otherwise, its default value ( (if the default is not defined, then the
     * method returns null).
     */
    public static String property(String key) {
        return props.getProperty(key);
    }

    /**
     * Add a new value to property
     *
     * @param type
     * @param schemaLocation
     */
    public static void addUserProperty2(FileType type, String schemaLocation) {
        String prop = props.getProperty("schemaLocation." + type);
        String value = props.getProperty(prop);
        props.setProperty(prop, value + " " + schemaLocation);
        saveToFile();
    }

    /**
     * Add a new value to property
     *
     * @param type
     * @param schemaLocation
     */
    public static void addUserProperty(String prop, String value) {
        String currentValue = props.getProperty(prop);
        if (currentValue == null) {
            props.setProperty(prop, value);
        } else {
            props.setProperty(prop, currentValue + " " + value);
        }
        saveToFile();
    }

    /**
     * Save properties to XML file (userProperties.xml)
     */
    private static void saveToFile() {
        try {
            File file = new File(appDir(), "userProperties.xml");
            OutputStream os = new FileOutputStream(file);
            props.storeToXML(os, null);
            os.close();
            Messages.info("Created new file: " + file.getAbsolutePath());
            FileType.reload();
        } catch (IOException ex) {
            Messages.severe(Settings.class.getName() + ": " + ex);
        }
    }
}
