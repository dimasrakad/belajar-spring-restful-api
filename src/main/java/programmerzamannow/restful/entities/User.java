package programmerzamannow.restful.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Transient
    private String oldEmail;

    @Transient
    private String oldName;

    @Transient
    private String oldPassword;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    private String token;

    @Column(name = "token_expired_at")
    private Long tokenExpiredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user")
    private List<Contact> contacts;

    @OneToMany(mappedBy = "user")
    private List<ContactCategory> categories;

    @PostLoad
    void loadOldValues() {
        this.oldEmail = this.email;
        this.oldName = this.name;
        this.oldPassword = this.password;
    }

    @PreUpdate
    protected void onUpdate() {
        if (!Objects.equals(oldEmail, email) ||
            !Objects.equals(oldName, name) ||
            !Objects.equals(oldPassword, password)) {
            this.updatedAt = LocalDateTime.now();
        }
    }
}
