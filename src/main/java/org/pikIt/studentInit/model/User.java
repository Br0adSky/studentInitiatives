package org.pikIt.studentInit.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Set;

@Data
@Entity
@Table(name = "usr")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Email(message = "Электронная почта заполнена неверно")
    @NotBlank(message = "Поле логина не может быть пустым")
    private String email;
    @NotBlank(message = "Поле пароля не может быть пустым")
    private String password;
    private boolean active;
    @NotBlank(message = "Поле имени не может быть пустым")
    @Size(min = 2, max=1478)
    private String name;
    @NotBlank(message = "Поле фамилии не может быть пустым")
    @Size(min = 2, max=1478)
    private String surname;


    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public User() {
    }

    public User(String email, String password, boolean active, String name, String surname) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.active = active;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getUsername() {
        return getName() + " " + getSurname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }


}