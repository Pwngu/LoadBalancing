package at.tgm.ablkreim.common.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseConfig {

    private static final Logger LOG = LogManager.getLogger("at.tgm.ablkreim.common.BaseConfig");


    /** The config file. */
    protected File configFile;
    /** The file header lines. */
    protected String[] fileHeader;

    private boolean initialized;

    /**
     * Constructs a new BaseConfig with the given config file and an empty file header.
     *
     * @param configFile the config file
     */
    protected BaseConfig(File configFile) {

        this(configFile, null);
    }

    /**
     * Constructs a new BaseConfig with the given config file and file header.
     *
     * @param configFile the config file
     * @param fileHeader the header string
     */
    protected BaseConfig(File configFile, String fileHeader) {

        this.configFile = configFile;
        if(fileHeader != null)
            this.fileHeader = fileHeader.split("\n");
        else
            this.fileHeader = new String[0];

        initialized = false;
    }

    /**
     * Initializes the config.
     *
     * If the config file exists its contents will be loaded into RAM, if it doesn't the file
     * will be created and the default values defined in the config will be assigned.
     *
     * @throws IOException                      if a IO error occurs during file operation
     * @throws InvalidConfigurationException    if the config file already exists and the
     *                                          configuration is invalid
     */
    public final void init() throws IOException {

        LOG.debug("Initializing config: " + getClass().getName());
        try {

            if(configFile.createNewFile()) {

                saveToFile();
                LOG.info("Successfully created and initialized config file: " + getClass().getSimpleName());
            } else if(configFile.isFile()) {

                loadFromFile();
                LOG.info("Successfully initialized config file: " + getClass().getSimpleName());
            } else {

                LOG.fatal("Fatal error during config initialization");
                throw new IOException("File name already exists and is directory");
            }
        } catch (IOException ex) {

            LOG.fatal("IOException during config initialization");
            throw ex;
        } catch(Exception ex) {

            LOG.fatal("Unknown Exception during config initialization");
            throw new InvalidConfigurationException("Couldn't init config", ex);
        }

        initialized = true;
    }

    /**
     * Gets the values of all fields annotated with {@link ConfigOption}.
     *
     * The returned map is filled with the field's ConfigOption annotations'
     * name (the value attribute) as key and the field's value as value.
     *
     * @return a map of the values with the value's name as key
     */
    protected final Map<ConfigOption, Object> getConfigFields() {

        LinkedHashMap<ConfigOption, Object> map = new LinkedHashMap<>();

        try {

            for(Field field : getClass().getDeclaredFields()) {

                field.setAccessible(true);

                ConfigOption option = field.getAnnotation(ConfigOption.class);
                if(option != null && !Modifier.isFinal(field.getModifiers()))
                    map.put(option, field.get(this));
            }

            LOG.debug("Getting config fields from: " + getClass().getName());
            LOG.debug(map);

            return map;
        } catch(IllegalAccessException ex) {

            throw new RuntimeException(ex);
        }
    }

    /**
     * Sets the values of all fields annotated with {@link ConfigOption}.
     *
     * A value is set if the ConfigOption's value (name) and
     * the key of the given map are equal.
     *
     * @param map a map of the values with the value's name as key
     */
    protected final void setConfigFields(Map<String, Object> map) {

        LOG.debug("Setting config fields from: " + getClass().getName());
        LOG.debug(map);

        try {

            for(Field field : getClass().getDeclaredFields()) {

                field.setAccessible(true);

                ConfigOption option = field.getAnnotation(ConfigOption.class);
                if(option != null) {

                    if(map.containsKey(option.value()))
                        field.set(this, map.get(option.value()));
                }
            }
        } catch(IllegalAccessException ex) {

            throw new RuntimeException(ex);
        }
    }

    /**
     * Saves the config currently in RAM to disk and overrides any other changes.
     * When a Exception is thrown, nothing will be changed in the file.
     *
     * @throws InvalidConfigurationException if the config stored in RAM is invalid
     * @throws IllegalStateException if the file wasn't initialized with the init() method
     */
    public final void save() throws IOException {

        if(!initialized) throw new IllegalStateException("Config not initialized!");

        LOG.debug("Saving config: " + getClass().getName());
        saveToFile();
    }

    /**
     * Stores the data of the config file in ram, and overrides any other changes.
     * When a Exception is thrown, nothing will be changed in RAM.
     *
     * @throws InvalidConfigurationException if the config file is invalid
     */
    public final void load() throws IOException {

        if(!initialized) throw new IllegalStateException("Config not initialized!");

        LOG.debug("Loading config: " + getClass().getName());
        loadFromFile();
    }

    /**
     * Saves all config values into the config file.
     *
     * An implementation of this method should save all values
     * from {@link #getConfigFields()} into the config file.
     *
     * @throws IOException if an IOException occurs during saving
     * @throws InvalidConfigurationException if the configuration currently saved in RAM is invalid
     */
    protected abstract void saveToFile() throws IOException;

    /**
     * Loads all config values from the config file.
     *
     * An implementation of this method should save all values from the
     * config file via {@link #setConfigFields(Map)} into the config file's fields.
     *
     * @throws IOException if an IOException occurs during saving
     * @throws InvalidConfigurationException if the configuration currently saved in RAM is invalid
     */
    protected abstract void loadFromFile() throws IOException;
}
