package account;

import account.businesslayer.Role;
import account.businesslayer.UserInfo;
import account.businesslayer.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Service
public class UserInfoDetailsService implements UserDetailsService {
    @Autowired
    UserService userService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo fromDb = userService.findByEmailIgnoreCase(email);
        if (fromDb == null) {
            throw new UsernameNotFoundException("Not founded: " + email);
            // throw new UserNotFoundException();
        } // else if (!fromDb.isAccountNonLocked()) {
          //  fromDb.setAccountNonLocked(true);
          //  fromDb.setFailedAttempt(0);
//
          //  return new UserInfoDetailsImpl(fromDb);
          // } else {
            return new UserInfoDetailsImpl(fromDb);



    }

    private Collection<GrantedAuthority> getAuthorities(UserInfo fromDb) {
        Set<Role> userRoles = fromDb.getUserRoles();


        Collection<GrantedAuthority> authorities = new ArrayList<>(userRoles.size());

        for (Role role : userRoles) {
            authorities.add(new SimpleGrantedAuthority(role.getCode()));
        }

        return authorities;


    }
}

