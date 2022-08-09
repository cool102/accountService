package account;

import account.businesslayer.UserInfo;
import account.businesslayer.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserInfoDetailsService implements UserDetailsService {
    @Autowired
    UserInfoService service;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo userinfo = service.findByEmailIgnoreCase(email);
        if (userinfo == null) {
            throw new UsernameNotFoundException("Not found: " + email);
        }
        return new UserInfoDetailsImpl(userinfo);
    }
}
