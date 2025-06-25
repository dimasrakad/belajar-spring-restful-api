package programmerzamannow.restful.models.contacts;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelectContactsRequest {
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 20)
    private List<String> ids;
}
