package programmerzamannow.restful.models.contactcategories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateContactCategoryRequest {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private CategoryColor color;
}
