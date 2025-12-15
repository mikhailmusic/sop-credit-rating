package rut.miit.sopcreditrating.util;

import rut.miit.sopcontracts.exception.BusinessLogicException;

public final class EnumUtils {

    public static <E extends Enum<E>> E parseEnumOrThrow(Class<E> clazz, String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessLogicException("Field '" + fieldName + "' is required");
        }
        try {
            return Enum.valueOf(clazz, value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            String allowed = String.join(", ",
                    java.util.Arrays.stream(clazz.getEnumConstants())
                            .map(Enum::name)
                            .toList()
            );
            throw new BusinessLogicException("Invalid value for '" + fieldName + "': " + value +
                    ". Allowed values: [" + allowed + "]");
        }
    }
}
