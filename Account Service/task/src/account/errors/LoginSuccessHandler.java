package account.errors;

import account.UserInfoDetailsImpl;
import account.businesslayer.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        UserInfoDetailsImpl details =  (UserInfoDetailsImpl) authentication.getPrincipal();
        if (details.getFailedAttempts() > 0) {
            userService.resetFailedAttempts(details.getEmail());
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

}
