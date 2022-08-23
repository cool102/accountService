package account.errors;

import account.businesslayer.EventsService;
import account.businesslayer.SecurityEvent;
import account.businesslayer.UserInfo;
import account.businesslayer.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    UserService userService;

    @Autowired
    EventsService eventsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);


    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {


        String header = request.getHeader("Authorization");
        if (!Objects.isNull(header)) {
            String object = request.getRequestURI();
            String email = getEmailFromHeader(header);
            String subject = email;
            UserInfo userInfo = userService.findByEmailIgnoreCase(email);

            if (userInfo != null) { // эта ветка если email был правильный и пользователя  нашли
                if (userInfo.isAccountNonLocked()) { //пользователь есть и аккаунт не блокирован

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

                    response.setStatus(401);
                    response.getOutputStream().println(new ObjectMapper().writeValueAsString(Map.of(
                            "status", response.getStatus(), "error", "Unauthorized", "path", request.getRequestURI(), "message", "Something wrong with credentials")));

                } else { ////пользователь есть НО аккаунт блокирован
                    response.setStatus(401);
                    response.getOutputStream().println(new ObjectMapper().writeValueAsString(Map.of(
                            "status", response.getStatus(), "error", "Unauthorized", "path", request.getRequestURI(), "message", "User account is lockedddddddddddddddd")));

                }
            } // эта ветка если email был неправильный и пользователя не нашли
            else {
                LOGGER.info("LOGIN_FAILED");
                SecurityEvent loginFailedEvent = new SecurityEvent(
                        LocalDateTime.now(), "LOGIN_FAILED", subject.toLowerCase(), object, object);
                eventsService.saveEvent(loginFailedEvent);
                response.setStatus(401);
                response.getOutputStream().println(new ObjectMapper().writeValueAsString(Map.of(
                        "status", response.getStatus(), "error", "Unauthorized", "path", request.getRequestURI(), "message", "User not found")));


                // throw new UserNotFoundException();
                //
                // userService.increaseFailedAttempts(userInfo);

            }
//работающий ответ

        } else {


            response.setStatus(401);
            response.getOutputStream().println(new ObjectMapper().writeValueAsString(Map.of(
                    "status", response.getStatus(), "error", "Unauthorized3", "path", request.getRequestURI(), "message", "User account is locked")));
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