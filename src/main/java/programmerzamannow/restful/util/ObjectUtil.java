package programmerzamannow.restful.util;

import java.util.function.Consumer;

public class ObjectUtil {
    /**
     * Set value if not null
     * @param <T> type
     * @param value value
     * @param consumer consumer
     */
    public static <T> void setIfNotNull(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }
}