package account.errors;

import account.UserInfoDetailsImpl;
import account.businesslayer.EventsService;
import account.businesslayer.SecurityEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private  static  final  Logger LOGGER = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    EventsService eventsService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LOGGER.info("ACCESS_DENIED");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDetailsImpl details = (UserInfoDetailsImpl) auth.getPrincipal();
        SecurityEvent accessDeniedEvent = new SecurityEvent(
                LocalDateTime.now(),"ACCESS_DENIED",
                details.getEmail().toLowerCase(),request.getRequestURI(),request.getRequestURI());
        eventsService.saveEvent(accessDeniedEvent);

        response.setStatus(HttpStatus.FORBIDDEN.value());
        Map<String, Object> body = new HashMap<>();
        body.put(
                "timestamp",Calendar.getInstance().getTime().toString() //LocalDateTime.now().toString()
                );
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        body.put(
                "message",
                "Access Denied!");
        body.put("path", request.getRequestURI()); //.replace("uri=", ""));//+request.getServletPath()//request.getContextPath()+request.getPathInfo()+request.getPathTranslated()
        response.getOutputStream()
                .println(objectMapper.writeValueAsString(body));
    }


}