package programmerzamannow.restful.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityUtil {
    public static Set<String> getAllowedSortFields(Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> !isRelationshipField(field))
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

    private static boolean isRelationshipField(Field field) {
        return field.isAnnotationPresent(jakarta.persistence.OneToOne.class) ||
                field.isAnnotationPresent(jakarta.persistence.OneToMany.class) ||
                field.isAnnotationPresent(jakarta.persistence.ManyToOne.class) ||
                field.isAnnotationPresent(jakarta.persistence.ManyToMany.class);
    }
}
