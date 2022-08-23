package account.errors;

import account.businesslayer.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Component
public class AuthenticationFailureListener implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFailureListener.class);
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @Autowired
    private UserService userService;
    @Autowired
    private EventsService eventsService;

    @SneakyThrows
    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        String email = e.getAuthentication().getPrincipal().toString();
        // UserInfoDetailsImpl details = (UserInfoDetailsImpl)e.getAuthentication().getPrincipal();

        //  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //  UserInfoDetailsImpl details = (UserInfoDetailsImpl)authentication.getPrincipal();
        String object = request.getRequestURI();
        String subject = email;
        UserInfo userInfo = userService.findByEmailIgnoreCase(email);
        if (userInfo != null) {

            if (userInfo.isAccountNonLocked() & !isAdmin(userInfo) ) {
                if (userInfo.getFailedAttempt() < UserService.MAX_FAILED_ATTEMPTS) {
                    LOGGER.info("LOGIN_FAILED");
                    SecurityEvent loginFailedEvent = new SecurityEvent(
                            LocalDateTime.now(), "LOGIN_FAILED", subject.toLowerCase(), object, object);
                    eventsService.saveEvent(loginFailedEvent);
                    userService.increaseFailedAttempts(userInfo);

                } else {
                    LOGGER.info("LOGIN_FAILED");
                    SecurityEvent loginFailedEvent = new SecurityEvent(
                            LocalDateTime.now(), "LOGIN_FAILED", subject.toLowerCase(), object, object);
                    eventsService.saveEvent(loginFailedEvent);

                    LOGGER.info("BRUTE_FORCE");
                    SecurityEvent bruteForceEvent = new SecurityEvent(
                            LocalDateTime.now(), "BRUTE_FORCE", userInfo.getEmail().toLowerCase(),
                            request.getRequestURI(), request.getRequestURI()
                    );
                    eventsService.saveEvent(bruteForceEvent);
                    userService.lock(userInfo, request);
                    userService.increaseFailedAttempts(userInfo);
                }
            }
        } else {
            LOGGER.info("LOGIN_FAILED");
            SecurityEvent loginFailedEvent = new SecurityEvent(
                    LocalDateTime.now(), "LOGIN_FAILED", subject.toLowerCase(), object, object);
            eventsService.saveEvent(loginFailedEvent);
          //response.setStatus(401);
          //response.getOutputStream().println(new ObjectMapper().writeValueAsString(Map.of(
          //        "status", response.getStatus(), "error", "Unauthorized", "path", request.getRequestURI(), "message", "User not found")));


        }


    }

    private static String getEmailFromHeader(String header) {
        String[] split = header.split("\\s");
        byte[] decoded = Base64.getDecoder().decode(split[1]);
        String decodedAsString = new String(decoded);
        String[] split1 = decodedAsString.split(":");
        return split1[0];
    }

    private boolean isAdmin(UserInfo userInfo){
        return  userInfo.getUserRoles().stream().anyMatch(r->r.getCode().equalsIgnoreCase("role_administrator"));
    }
}
