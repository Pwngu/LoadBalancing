package at.tgm.ablkreim.common.config;

import java.io.IOException;

/**
 * The Exception used by config classes to indicate, that a given configuration is invalid.
 *
 * See {@link YamlConfig}
 *
 * @author Pwngu
 * @version 1.0
 */
public class InvalidConfigurationException extends IOException {

    /**
     * Constructs a new InvalidConfigurationException with a detail message.
     *
     * @param message a detail message, see {@link Throwable#getMessage()}
     */
    public InvalidConfigurationException(String message) {

        super(message);
    }

    /**
     * Constructs a new InvalidConfigurationException with a cause.
     *
     * @param cause the Throwable, which caused this InvalidConfigurationException to be thrown
     */
    public InvalidConfigurationException(Throwable cause) {

        super(cause);
    }

    /**
     * Constructs a new InvalidConfigurationException with a detail message and cause.
     *
     * @param message a detail message, see {@link Throwable#getMessage()}
     * @param cause the Throwable, which caused this InvalidConfigurationException to be thrown
     */
    public InvalidConfigurationException(String message, Throwable cause) {

        super(message, cause);
    }
}
