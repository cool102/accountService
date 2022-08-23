package account.errors;

import account.UserInfoDetailsImpl;
import account.businesslayer.UserInfo;
import account.businesslayer.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Objects;

@Component
public class AuthenticationSuccessEventListener implements
        ApplicationListener<AuthenticationSuccessEvent> {
    @Autowired
    UserService userService;

    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent event) {

            Authentication auth = event.getAuthentication();
            UserInfoDetailsImpl details = (UserInfoDetailsImpl) auth.getPrincipal();

            if (details.getFailedAttempts()>0) {
                UserInfo userInfo1 = userService.findByEmailIgnoreCase(details.getEmail());
                userService.unLock1(userInfo1);
            }
        }


    private static String getEmailFromHeader(String header) {
        String[] split = header.split("\\s");
        byte[] decoded = Base64.getDecoder().decode(split[1]);
        String decodedAsString = new String(decoded);
        String[] split1 = decodedAsString.split(":");
        return split1[0];
    }

}
