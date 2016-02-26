package at.tgm.ablkreim.common.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class YamlConfig extends BaseConfig {

    private static final Logger LOG = LogManager.getLogger("at.tgm.ablkreim.common.YamlConfig");


    private static Map<Class<?>, Represent> representMap = new HashMap<>();

    /**
     * Registers a custom {@link Represent} for a class.
     *
     * @param clazz the class the Represent is for
     * @param represent the Represent for the class
     */
    public static void registerRepresent(Class<?> clazz, Represent represent) {

        LOG.info("Registering custom Representer for: " + clazz.getName());

        representMap.put(clazz, represent);
    }


    private Yaml yaml;

    /**
     * Constructs a new YamlConfig with the given config file and an empty file header.
     *
     * @param configFile the config file
     */
    protected YamlConfig(File configFile) {

        this(configFile, null);
    }

    /**
     * Constructs a new YamlConfig with the given config file and file header.
     *
     * @param configFile the config file
     * @param fileHeader the header string
     */
    protected YamlConfig(File configFile, String fileHeader) {

        super(configFile, fileHeader);

        YamlRepresenter yamlRepresenter = new YamlRepresenter();
        if(!representMap.isEmpty()) {

            for(Map.Entry<Class<?>, Represent> entry : representMap.entrySet()) {

                yamlRepresenter.putRepresenter(entry.getKey(), entry.getValue());
            }
        }

        DumperOptions yamlOptions = new DumperOptions();
        yamlOptions.setIndent(2);
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        yaml = new Yaml(yamlRepresenter, yamlOptions);
    }

    @Override
    protected void loadFromFile() throws InvalidConfigurationException {

        try(FileInputStream fis = new FileInputStream(configFile)) {

            Object obj = yaml.load(fis);
            LOG.debug(obj);

            //noinspection unchecked
            setConfigFields((Map<String, Object>) obj);
//			LOG.debug("Successfully loaded object: ");
//			LOG.debug(obj);
        } catch (IOException | ClassCastException ex) {

            throw new InvalidConfigurationException("Could not load YAML", ex);
        } catch(YAMLException ex) {

            throw new InvalidConfigurationException("Error parsing YAML" + "\n" + ex.getMessage());
        }
    }

    @Override
    protected void saveToFile() throws IOException {

        try(FileWriter writer = new FileWriter(configFile)) {

            Map<ConfigOption, Object> map = getConfigFields();

            StringWriter sw = new StringWriter();

            if(fileHeader.length > 0) {

                for(String str : fileHeader) {

                    sw.write("# ");
                    sw.write(str);
                    sw.write('\n');
                }
                sw.write('\n');
                sw.write('\n');
            }
            writer.write(sw.toString());

            yaml.dump(getTreeValueMap(map), writer);

        } catch(FileNotFoundException ex) {

            LOG.fatal("Couldn't find config file! Run init() first!");
            throw ex;
        } catch(IOException ex) {

            LOG.fatal("IOException whilst trying to write to config file!");
            throw new InvalidConfigurationException("IOException whilst trying to write to config file", ex);
        }
    }

    private Map<String, Object> getTreeValueMap(Map<ConfigOption, Object> values) {

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        for(Map.Entry<ConfigOption, Object> entry : values.entrySet()) {

            if(entry.getValue() instanceof ConfigSection)
                map.put(entry.getKey().value(), ((ConfigSection) entry.getValue()).getTreeValueMap());
            else
                map.put(entry.getKey().value(), entry.getValue());
        }

        return map;
    }

    private static class YamlRepresenter extends Representer {

        public void putRepresenter(Class<?> clazz, Represent represent) {

            this.representers.put(clazz, represent);
            LOG.debug("Registered representer for class: " + clazz.getName());
        }
    }
}
