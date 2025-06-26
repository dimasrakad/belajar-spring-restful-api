package programmerzamannow.restful.models.contactcategories;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import programmerzamannow.restful.enums.CategoryColor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateContactCategoryRequest {
    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private CategoryColor color;
}
