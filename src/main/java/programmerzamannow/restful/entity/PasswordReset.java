package programmerzamannow.restful.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "password_resets")
public class PasswordReset {
    @Id
    @Column(name = "username")
    private String username;

    private String token;

    @Column(name = "expired_at")
    private Long expiredAt;

    @OneToOne
    @MapsId
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;
}
