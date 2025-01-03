package com.example.pillyohae.global.util;

import com.example.pillyohae.domain.user.entity.User;
import com.example.pillyohae.global.constants.GlobalConstants;
import com.example.pillyohae.global.dto.Authentication;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SessionUtil {
    private final HttpSession session;

    public SessionUtil(HttpSession session) {
        this.session = session;
    }

    public Long getUserId() {
        Authentication authentication = (Authentication) session.getAttribute(GlobalConstants.USER_AUTH);
        Long userId = authentication.getId();

        if(userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        return userId;
    }

    public void checkAuthorization(User user) {
        Long loginEmail = getUserId();
        Long verificationEmail = user.getId();

        if(!loginEmail.equals(verificationEmail)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "권한이 없습니다.");
        }
    }
}
