package programmerzamannow.restful.util;

import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpecificationUtil {
    /**
     * Adds a LIKE predicate if the given value is present.
     *
     * @param fieldValue      The filter value from the request
     * @param fieldName       The field name to match in the entity
     * @param root            The Root<T> of the current specification
     * @param criteriaBuilder CriteriaBuilder from the specification
     * @param predicates      List of predicates to add to
     * @param <T>             The entity type (e.g., Address, User, Order)
     */
    public static <T> void addLikePredicateIfPresent(String fieldValue, String fieldName, Root<T> root,
            CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (fieldValue != null && !fieldValue.isBlank()) {
            predicates.add(criteriaBuilder.like(root.get(fieldName), "%" + fieldValue + "%"));
        }
    }
}
