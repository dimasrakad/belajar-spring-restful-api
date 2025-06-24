package programmerzamannow.restful.model.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchContactRequest {
    private String name;
    private String email;
    private String phone;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
