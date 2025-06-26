package programmerzamannow.restful.models.contacts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateContactRequest {
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Email
    @Size(max = 100)
    private String email;

    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
    @Size(max = 100)
    private String phone;

    private Long categoryId;
}
