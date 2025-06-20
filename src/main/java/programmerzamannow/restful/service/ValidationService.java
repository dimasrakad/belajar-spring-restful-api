package programmerzamannow.restful.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import programmerzamannow.restful.util.EntityUtil;

@Service
public class ValidationService {
    @Autowired
    private Validator validator;

    public void validate(Object request) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);

        if (constraintViolations.size() != 0) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    public void validateSort(String sortBy, String sortDirection, Class<?> entityClass) {
        Set<String> allowedFields = EntityUtil.getAllowedSortFields(entityClass);
        // Validate sortBy
        if (sortBy != null && !sortBy.isBlank() && !allowedFields.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sortBy field. Allowed values: " + String.join(", ", allowedFields));
        }

        // Validate sortDirection
        try {
            if (sortDirection != null && !sortDirection.isBlank()) {
                Sort.Direction.fromString(sortDirection);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid sortDirection. Use 'asc' or 'desc'");
        }
    }
}
