package programmerzamannow.restful.models.addresses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {
    private String id;
    private String street;
    private String city;
    private String province;
    private String country;
    private String postalCode;
}
