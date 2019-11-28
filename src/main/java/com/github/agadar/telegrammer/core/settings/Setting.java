package com.github.agadar.telegrammer.core.settings;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * An application setting.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 * @param <T> The value type of the setting.
 */
@Slf4j
@RequiredArgsConstructor
public class Setting<T> {

    @Getter
    @NonNull
    private final String key;
    @NonNull
    private final Class<T> type;
    @NonNull
    private final Function<String, T> parser;
    @NonNull
    private final Function<T, String> stringifier;

    @Getter
    @NonNull
    private T value;

    /**
     * Sets the value of this setting by parsing the given string.
     * 
     * @param value The string to parse and then set this value to.
     * @return True if parsing did not result in an exception, but in a non-null
     *         value, and this setting's value was set to it, else false.
     */
    public boolean setValueFromString(@NonNull String value) {
        T parsed;
        try {
            parsed = parser.apply(value);

        } catch (Exception ex) {
            log.error(createCouldntParseMessage(value), ex);
            return false;
        }
        if (parsed == null) {
            log.error(createCouldntParseMessage(value));
            return false;
        }
        this.value = parsed;
        return true;
    }

    /**
     * Sets the value of this setting to the given value.
     * 
     * @param value The value to set this setting to.
     * @return True if the value is set, else false if the given value is not of
     *         this setting's value type.
     */
    public boolean setValue(@NonNull Object value) {
        if (isOfType(value.getClass())) {
            this.value = type.cast(value);
            return true;
        }
        log.error("Couldn't set value of setting '{}': value is of type '{}' but setting is of type '{}'", key,
                value.getClass(), type);
        return false;
    }

    /**
     * Checks whether or not this setting's value is of the given type.
     * 
     * @param type The type to compare to this setting's value type.
     * @return True if this setting's value is of the given type, else false.
     */
    public boolean isOfType(Class<?> type) {
        return this.type.isAssignableFrom(type);
    }

    /**
     * Gets the stringified version of this setting's current value.
     * 
     * @return The stringified version of this setting's current value.
     */
    public String getValueAsString() {
        return stringifier.apply(value);
    }

    private String createCouldntParseMessage(String value) {
        return String.format("Couldn't parse string '%s' to type '%s' for setting '%s'", value, type, key);
    }
}
