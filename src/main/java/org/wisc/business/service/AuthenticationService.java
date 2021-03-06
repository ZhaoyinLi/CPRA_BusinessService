package org.wisc.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wisc.business.authentication.SecurityUtil;
import org.wisc.business.model.UserModel.User;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;

@Service
public class AuthenticationService {
    @Autowired
    UserService userService;

    public String login(User user) {
        User userFromDatabase = null;
        if (user.getFavorite() == null)
            user.setFavorite(new LinkedList<>());
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            // login with email
            userFromDatabase =
                    userService.findRawByEmail(user.getEmail());
        } else if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            userFromDatabase =
                    userService.findRawByUsername(user.getUsername());
        }
        if (userFromDatabase == null)
            return null;
        if (!SecurityUtil.hashPassword(user.getPassword(),
                userFromDatabase.getSalt()).equals(userFromDatabase.getPassword()))
            return "Invalid Password";
        return SecurityUtil.generateToken(userFromDatabase);
    }

    public boolean isValidToken(String token) {
        return getCurrentUser(token) != null;
    }

    public User getCurrentUser(String token) {
        if (token == null)
            return null;
        User user =
                userService.findRawById(SecurityUtil.getUserIdFromToken(token));
        if (user == null)
            return null;
        return SecurityUtil.verifyTokenWithPassword(token,
                user.getPassword())?user:null;
    }

    @Deprecated
    public boolean isLoggedIn(HttpServletRequest request) {
        if (request == null)
            return false;
        String token  =request.getHeader("token");
        if (token == null)
            return false;
        return isValidToken(token);
    }
}
