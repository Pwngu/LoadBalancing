package at.tgm.ablkreim.common.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigSection {

    /** The Character used to separate paths to values. */
    public static final char SECTION_SEPARATOR_CHAR = '.';

    private String name;
    private ConfigSection parent;
    private Map<String, Object> values;
    private Map<String, String[]> comments;

    /**
     * Creates a new, empty root ConfigSection ConfigSection.
     */
    public ConfigSection() {

        parent = null;
        values = new LinkedHashMap<>();
    }

    /**
     * Creates a new root ConfigSection and all sub ConfigSections, defined by the given map.
     *
     * If map is the return value of {@link ConfigSection#getFlatValueMap()}
     * the ConfigSection and this object are completely equal.
     *
     * @param map the map to create the ConfigSection from
     */
    public ConfigSection(Map<ConfigOption, Object> map) {

        this();

        for(Map.Entry<ConfigOption, Object> entry : map.entrySet()) {

            internAddValue(entry.getKey().value(), entry.getValue());
            internAddComment(entry.getKey().value(), entry.getKey().comment().split("\n"));
        }
    }

    private ConfigSection(ConfigSection parent, String name) {

        this.parent = parent;
        this.name = name;
        values = new LinkedHashMap<>();
    }

    /**
     * Returns the absolute path to this ConfigSection or an empty String, if this is a rootConfigSection ConfigSection.
     *
     * @return the absolute path to this ConfigSection or an empty String
     */
    public String getAbsolutePath() {

        return parent == null ? "" : parent.getAbsolutePath() + SECTION_SEPARATOR_CHAR + name;
    }

    /**
     * Returns whether this ConfigSection is a rootConfigSection section.
     *
     * @return whether this ConfigSection is a rootConfigSection section
     */
    public boolean isRoot() {

        return parent == null;
    }

    /**
     * Returns a Map containing all values of this and all sub ConfigSections.
     *
     * Note:
     * <ul>
     * <li>The keys of the Map are the absolute paths to the value.</li>
     * <li>Map and key values won't be split apart</li>
     * <li>There aren't any sub maps in this map</li>
     * </ul>
     *
     * @return a Map containing all values of this and all sub ConfigSections
     */
    public Map<String, Object> getFlatValueMap() {

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        for(Map.Entry<String, Object> entry : values.entrySet()) {

            if(entry.getValue() instanceof ConfigSection)
                map.putAll(((ConfigSection) entry.getValue()).getFlatValueMap());
            else
                map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    public Map<String, Object> getTreeValueMap() {

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        for(Map.Entry<String, Object> entry : values.entrySet()) {

            if(entry.getValue() instanceof ConfigSection)
                map.put(entry.getKey(), ((ConfigSection) entry.getValue()).getTreeValueMap());
            else
                map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    /**
     * Adds a sub ConfigSection this ConfigSection, identified by the given name.
     *
     * The name can be a <code>'.'</code> separated path to the ConfigSection, ending with the final ConfigSection's name.<br>
     * Note:
     * <ul>
     * <li>If the parent ConfigSection doesn't exist it and all other necessary parent ConfigSections will be created.</li>
     * <li>If a name used in the name hierarchy is already used, a IllegalArgumentException is thrown.</li>
     * </ul>
     *
     * @param name the name of the new ConfigSection
     * @return the newly created ConfigSection
     * @throws IllegalArgumentException if the name, or a part of its hierarchy is already in use,
     *                                  or if the name is invalid
     */
    public ConfigSection addConfigSection(String name) {

        if(name.charAt(0) == SECTION_SEPARATOR_CHAR || name.charAt(name.length() - 1) == SECTION_SEPARATOR_CHAR)
            throw new IllegalArgumentException("ConfigSection name mustn't start or end with a dot: " + name);

        int dotIndex = name.indexOf(SECTION_SEPARATOR_CHAR);
        if(dotIndex != -1) {

            String sectionName = name.substring(0, dotIndex);

            Object value = values.get(sectionName);

            if(value == null) {

                if(values.containsKey(sectionName)) {

                    throw new IllegalArgumentException(
                            "The value \"" + sectionName + "\" is already in use, but assigned to no value");
                } else {

                    ConfigSection section = new ConfigSection(this, sectionName).addConfigSection(name.substring(dotIndex + 1));
                    values.put(name, section);
                    return section;
                }

            } else if(value instanceof ConfigSection) {

                return ((ConfigSection) value).addConfigSection(name.substring(dotIndex + 1));

            } else throw new IllegalArgumentException(
                    "The value \"" + sectionName + "\" is already assigned to a value of: "
                            + value.getClass());
        } else {

            Object value = values.get(name);

            if(value == null) {

                ConfigSection section = new ConfigSection(this, name);
                values.put(name, section);
                return section;
            } else if(value instanceof ConfigSection) {

                return (ConfigSection) value;
            } else throw new IllegalArgumentException(
                    "The value \"" + name + "\" is already assigned to a value of: " + value.getClass());
        }
    }

    private void internAddValue(String name, Object value) {

        if(name.charAt(0) == SECTION_SEPARATOR_CHAR || name.charAt(name.length() - 1) == SECTION_SEPARATOR_CHAR)
            throw new IllegalArgumentException("Value name mustn't start or end with a dot: " + name);

        int dotIndex = name.indexOf(SECTION_SEPARATOR_CHAR);
        if(dotIndex != -1) {

            String sectionName = name.substring(0, dotIndex);

            Object checkValue = values.get(sectionName);

            if(checkValue == null) {

                if(values.containsKey(sectionName))
                    throw new IllegalArgumentException(
                            "Couldn't add value: \"" + name + "\" it is already assigned to null");
                else {

                    int lastDotIndex = name.lastIndexOf(SECTION_SEPARATOR_CHAR);
                    if(lastDotIndex != dotIndex) {

                        addConfigSection(
                                name.substring(0, lastDotIndex))
                                .internAddValue(name.substring(lastDotIndex + 1),
                                        value);
                    } else {

                        addConfigSection(
                                name.substring(0, dotIndex))
                                .internAddValue(name.substring(lastDotIndex + 1),
                                value);
                    }
                }
            } else if(checkValue instanceof ConfigSection) {

                ((ConfigSection) checkValue).internAddValue(name.substring(dotIndex + 1), value);
            } else throw new IllegalArgumentException(
                    "Couldn't find child section for: \"" + name + "\" it is a value already assigned to a value of "
                            + checkValue.getClass().getName());
        } else {

            Object checkValue = values.get(name);

            if(checkValue == null) {

                if(values.containsKey(name))
                    throw new IllegalArgumentException(
                            "Couldn't add value: \"" + name + "\" it is a value already assigned to null");
                else
                    values.put(name, value);
            } else if(checkValue instanceof ConfigSection)
                throw new IllegalArgumentException("Couldn't add value: \"" + name + "\" it is ConfigSection already in use");
            else throw new IllegalArgumentException(
                        "Couldn't add value: \"" + name + "\" it is a value already assigned to a value of: "
                                + checkValue.getClass().getName());
        }
    }

    private void internAddComment(String name, String[] commentLines) {

        //TODO add comment
    }

    /**
     * Returns the saved comment bound to the given value.
     *
     * @param name the name of the value whose comment will be returned
     * @return the comment lines of the value with the given name
     */
    public String[] getComment(String name) {

        //TODO return comment
        return null;
    }

    /**
     * Returns whether the value of the given name is already in use.
     *
     * If this method returns true every addValue(String, ...) method of this ConfigSection
     * won't throw an Exception, when using the same name.
     *
     * @param name the name of the value ot check
     * @return whether the value is already in use
     */
    public boolean isUsed(String name) {

        if(name.charAt(0) == SECTION_SEPARATOR_CHAR || name.charAt(name.length() - 1) == SECTION_SEPARATOR_CHAR)
            throw new IllegalArgumentException("ConfigSection name mustn't start or end with a dot: " + name);

        if(name.indexOf(SECTION_SEPARATOR_CHAR) != -1) {

            int dotIndex = name.indexOf(SECTION_SEPARATOR_CHAR);
            String sectionName = name.substring(0, dotIndex);

            Object value = values.get(sectionName);

            if(value == null)
                //if there is no key with the name, the name can be safely used
                return values.containsKey(sectionName);
            else if(value instanceof ConfigSection)
                return ((ConfigSection) value).isUsed(name.substring(dotIndex + 1));
            else return false;
        } else {

            Object value = values.get(name);
            return value == null && values.containsKey(name);
        }
    }

    /**
     * Gets the value of the given name.
     *
     * @param name the name of the value
     * @return the value of the given name, or null if none is assigned
     * @throws IllegalArgumentException if the name points at no value, or is invalid
     */
    public Object getValue(String name) {

        if(name.charAt(0) == SECTION_SEPARATOR_CHAR || name.charAt(name.length() - 1) == SECTION_SEPARATOR_CHAR)
            throw new IllegalArgumentException("ConfigSection name mustn't start or end with a dot: " + name);

        int dotIndex = name.indexOf(SECTION_SEPARATOR_CHAR);
        if(dotIndex != -1) {

            String sectionName = name.substring(0, dotIndex);

            Object value = values.get(sectionName);

            if(value == null) {

                if(values.containsKey(sectionName))
                    throw new IllegalArgumentException(
                            "Couldn't find child section for: \"" + name + "\" it is a value already assigned to null");
                else
                    throw new IllegalArgumentException("Couldn't find value for: " + name);
            } else if(value instanceof ConfigSection) {

                return ((ConfigSection) value).getValue(name.substring(dotIndex + 1));
            } else throw new IllegalArgumentException(
                        "Couldn't find child section for: \"" + name + "\" it is a value already assigned to a value of "
                        + value.getClass().getName());
        } else {

            if(!values.containsKey(name))
                throw new IllegalArgumentException("Couldn't find value for: " + name);

            Object value = values.get(name);

            if(value instanceof ConfigSection)
                return ((ConfigSection) value).getFlatValueMap();
            else
                return value;
        }
    }

    /**
     * Adds a Byte value to this ConfigSection, identified by the given name.
     *
     * The name can be a <code>'.'</code> separated path to the value, ending with its name.<br>
     * Note:
     * <ul>
     * <li>If the parent ConfigSection doesn't exist it and all other necessary parent ConfigSections will be created.</li>
     * <li>If a name used in the name hierarchy is already used by a value, a IllegalArgumentException is thrown.</li>
     * </ul>
     *
     * @param name the name of the value
     * @param value the value
     * @throws IllegalArgumentException if the name, or a part of its hierarchy is already in use,
     *                                  or if the name is invalid
     */
    public void addValue(String name, byte value) { internAddValue(name, value); }

    /**
     * Adds a Short value to this ConfigSection, identified by the given name.
     *
     * The name can be a <code>'.'</code> separated path to the value, ending with its name.<br>
     * Note:
     * <ul>
     * <li>If the parent ConfigSection doesn't exist it and all other necessary parent ConfigSections will be created.</li>
     * <li>If a name used in the name hierarchy is already used by a value, a IllegalArgumentException is thrown.</li>
     * </ul>
     *
     * @param name the name of the value
     * @param value the value
     * @throws IllegalArgumentException if the name, or a part of its hierarchy is already in use,
     *                                  or if the name is invalid
     */
    public void addValue(String name, short value) { internAddValue(name, value); }

    /**
     * Adds a Integer value to this ConfigSection, identified by the given name.
     *
     * The name can be a <code>'.'</code> separated path to the value, ending with its name.<br>
     * Note:
     * <ul>
     * <li>If the parent ConfigSection doesn't exist it and all other necessary parent ConfigSections will be created.</li>
     * <li>If a name used in the name hierarchy is already used by a value, a IllegalArgumentException is thrown.</li>
     * </ul>
     *
     * @param name the name of the value
     * @param value the value
     * @throws IllegalArgumentException if the name, or a part of its hierarchy is already in use,
     *                                  or if the name is invalid
     */
    public void addValue(String name, int value) { internAddValue(name, value); }

    /**
     * Adds a Long value to this ConfigSection, identified by the given name.
     *
     * The name can be a <code>'.'</code> separated path to the value, ending with its name.<br>
     * Note:
     * <ul>
     * <li>If the parent ConfigSection doesn't exist it and all other necessary parent ConfigSections will be created.</li>
     * <li>If a name used in the name hierarchy is already used by a value, a IllegalArgumentException is thrown.</li>
     * </ul>
     *
     * @param name the name of the value
     * @param value the value
     * @throws IllegalArgumentException if the name, or a part of its hierarchy is already in use,
     *                                  or if the name is invalid
     */
    public void addValue(String name, long value) { internAddValue(name, value); }

    /**
     * Adds a Character value to this ConfigSection, identified by the given name.
     *
     * The name can be a <code>'.'</code> separated path to the value, ending with its name.<br>
     * Note:
     * <ul>
     * <li>If the parent ConfigSection doesn't exist it and all other necessary parent ConfigSections will be created.</li>
     * <li>If a name used in the name hierarchy is already used by a value, a IllegalArgumentException is thrown.</li>
     * </ul>
     *
     * @param name the name of the value
     * @param value the value
     * @throws IllegalArgumentException if the name, or a part of its hierarchy is already in use,
     *                                  or if the name is invalid
     */
    public void addValue(String name, char value) { internAddValue(name, value); }

    /**
     * Adds a Float value to this ConfigSection, identified by the given name.
     *
     * The name can be a <code>'.'</code> separated path to the value, ending with its name.<br>
     * Note:
     * <ul>
     * <li>If the parent ConfigSection doesn't exist it and all other necessary parent ConfigSections will be created.</li>
     * <li>If a name used in the name hierarchy is already used by a value, a IllegalArgumentException is thrown.</li>
     * </ul>
     *
     * @param name the name of the value
     * @param value the value
     * @throws IllegalArgumentException if the name, or a part of its hierarchy is already in use,
     *                                  or if the name is invalid
     */
    public void addValue(String name, float value) { internAddValue(name, value); }

    /**
     * Adds a Double value to this ConfigSection, identified by the given name.
     *
     * The name can be a <code>'.'</code> separated path to the value, ending with its name.<br>
     * Note:
     * <ul>
     * <li>If the parent ConfigSection doesn't exist it and all other necessary parent ConfigSections will be created.</li>
     * <li>If a name used in the name hierarchy is already used by a value, a IllegalArgumentException is thrown.</li>
     * </ul>
     *
     * @param name the name of the value
     * @param value the value
     * @throws IllegalArgumentException if the name, or a part of its hierarchy is already in use,
     *                                  or if the name is invalid
     */
    public void addValue(String name, double value) { internAddValue(name, value); }

    /**
     * Adds a String value to this ConfigSection, identified by the given name.
     *
     * The name can be a <code>'.'</code> separated path to the value, ending with its name.<br>
     * Note:
     * <ul>
     * <li>If the parent ConfigSection doesn't exist it and all other necessary parent ConfigSections will be created.</li>
     * <li>If a name used in the name hierarchy is already used by a value, a IllegalArgumentException is thrown.</li>
     * </ul>
     *
     * @param name the name of the value
     * @param value the value
     * @throws IllegalArgumentException if the name, or a part of its hierarchy is already in use,
     *                                  or if the name is invalid
     */
    public void addValue(String name, String value) { internAddValue(name, value); }

    /**
     * Adds a List to this ConfigSection, identified by the given name.
     *
     * The name can be a <code>'.'</code> separated path to the value, ending with its name.<br>
     * Note:
     * <ul>
     * <li>If the parent ConfigSection doesn't exist it and all other necessary parent ConfigSections will be created.</li>
     * <li>If a name used in the name hierarchy is already used by a value, a IllegalArgumentException is thrown.</li>
     * <li>If the contents of this List aren't primitive types, Strings or in turn Lists or Maps
     * containing only these types, a {@link InvalidConfigurationException} will be thrown
     * when trying to save the config!</li>
     * </ul>
     *
     * @param name the name of the value
     * @param value the value
     * @throws IllegalArgumentException if the name, or a part of its hierarchy is already in use,
     *                                  or if the name is invalid
     */
    public void addValue(String name, List<?> value) { internAddValue(name, value); }

    /**
     * Adds a Map to this ConfigSection, identified by the given name.
     *
     * The name can be a <code>'.'</code> separated path to the value, ending with its name.<br>
     * Note:
     * <ul>
     * <li>If the parent ConfigSection doesn't exist it and all other necessary parent ConfigSections will be created.</li>
     * <li>If a name used in the name hierarchy is already used by a value, a IllegalArgumentException is thrown.</li>
     * <li>If the contents of this Map (keys or values) aren't primitive types, Strings or in turn Lists or Maps
     * containing only these types, a {@link InvalidConfigurationException} will be thrown
     * when trying to save the config!</li>
     * </ul>
     *
     * @param name the name of the value
     * @param value the value
     * @throws IllegalArgumentException if the name, or a part of its hierarchy is already in use,
     *                                  or if the name is invalid
     */
    public void addValue(String name, Map<?, ?> value) { internAddValue(name, value); }
}
