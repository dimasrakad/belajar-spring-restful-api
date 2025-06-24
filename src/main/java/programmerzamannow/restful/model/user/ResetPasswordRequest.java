package programmerzamannow.restful.model.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordRequest {
    @NotBlank
    private String token;
    
    @NotBlank
    @Size(max = 100)
    private String newPassword;

    @NotBlank
    @Size(max = 100)
    private String confirmPassword;
}
