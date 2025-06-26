package programmerzamannow.restful.models.contactcategories;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import programmerzamannow.restful.enums.CategoryColor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactCategoryResponse {
    private Long id;
    private String name;
    private CategoryColor color;
}
