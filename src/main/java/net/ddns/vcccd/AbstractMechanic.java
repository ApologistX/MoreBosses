package net.ddns.vcccd;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for mechanics with common functionality
 */
public abstract class AbstractMechanic implements Mechanic {

    protected final Main main;
    protected final String id;
    protected final String description;

    public AbstractMechanic(Main main, String id, String description) {
        this.main = main;
        this.id = id;
        this.description = description;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean validateParameters(Map<String, Object> parameters) {
        // Default implementation: always valid
        // Override in subclasses for specific validation
        return true;
    }

    @Override
    public Map<String, Object> getDefaultParameters() {
        // Override in subclasses to provide defaults
        return new HashMap<>();
    }

    /**
     * Helper to get parameter as double
     */
    protected double getDouble(Map<String, Object> params, String key, double defaultValue) {
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Helper to get parameter as int
     */
    protected int getInt(Map<String, Object> params, String key, int defaultValue) {
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Helper to get parameter as boolean
     */
    protected boolean getBoolean(Map<String, Object> params, String key, boolean defaultValue) {
        Object value = params.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return defaultValue;
    }

    /**
     * Helper to get parameter as String
     */
    protected String getString(Map<String, Object> params, String key, String defaultValue) {
        Object value = params.get(key);
        return value != null ? value.toString() : defaultValue;
    }
}