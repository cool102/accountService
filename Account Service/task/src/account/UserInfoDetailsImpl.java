package account;

import account.businesslayer.Role;
import account.businesslayer.UserInfo;
import account.businesslayer.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class UserInfoDetailsImpl implements UserDetails {
    private final String name;
    private final String lastname;
    private final String email;
    private final String password;
    private final int failedAttempts;

    private final boolean accountNonLocked;

    private final Collection<GrantedAuthority> authorities;
    @Autowired
    UserService userService;

    private UserInfo userInfo;

    public UserInfoDetailsImpl(UserInfo userInfo) {

        this.name = userInfo.getName();
        this.lastname = userInfo.getLastname();
        this.email = userInfo.getEmail();
        this.password = userInfo.getPassword();
        this.authorities = setAuthorities(userInfo);
        this.accountNonLocked = userInfo.isAccountNonLocked();
        this.failedAttempts = userInfo.getFailedAttempt();

    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    private Collection<GrantedAuthority> setAuthorities(UserInfo userInfo) {
        Set<Role> userRoles = userInfo.getUserRoles();
        Collection<GrantedAuthority> authorities = new ArrayList<>(userRoles.size());

        for (Role role : userRoles) {
            authorities.add(new SimpleGrantedAuthority(role.getCode()));
        }
        return authorities;
    }


    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
