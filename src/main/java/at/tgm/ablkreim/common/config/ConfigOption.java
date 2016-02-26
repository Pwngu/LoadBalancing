package at.tgm.ablkreim.common.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration Annotation used to mark fields as config option.
 *
 * @author Pwngu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigOption {

    /**
     * The path to the config option separated by {@code '.'}.
     *
     * @return the path to the config option
     */
    String value();

    /**
     * The comment of the config option, defaults to {@code ""}.
     *
     * @return the comment of the config option
     */
    String comment() default "";
}
