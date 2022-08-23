package account.businesslayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "userinfo")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    @NotEmpty
    private String name;
    @Column
    @NotEmpty
    private String lastname;
    @Column
    @NotNull
    @Pattern(regexp = "[a-zA-Z]+@acme.com")
    private String email;
    @Column
    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
// – аннотация работает только тогда когда мы получаем объект из JSON
    private String password;

    @ManyToMany(cascade = {CascadeType.PERSIST,
            CascadeType.MERGE},fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> userRoles = new HashSet<>();

    @Column(name="failedAttempt")
    private int failedAttempt;

    @Column(name="accountNonLocked")
    private boolean accountNonLocked =true;



    public int getFailedAttempt() {
        return failedAttempt;
    }

    public void setFailedAttempt(int failedAttempt) {
        this.failedAttempt = failedAttempt;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }



    public UserInfo(long id, String name, String lastname, String email, String password, Set<Role> userRoles, int failedAttempt, boolean accountNonLocked, Date lockTime) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.userRoles = userRoles;
        this.failedAttempt = failedAttempt;
        this.accountNonLocked = accountNonLocked;

    }

    public UserInfo(long id, String name, String lastname, String email, Set<Role> userRoles) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.userRoles = userRoles;
    }

    public UserInfo(String name, String lastname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public UserInfo() {
    }

    public UserInfo(long id, String name, String lastname, String email) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
    }

    public Set<Role> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<Role> userRoles) {
        this.userRoles = userRoles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


}
