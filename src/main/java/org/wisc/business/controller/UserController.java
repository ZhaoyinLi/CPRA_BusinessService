package org.wisc.business.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.wisc.business.model.AjaxResponse;
import org.wisc.business.model.UserModel.User;
import org.wisc.business.service.DuplicateEmailException;
import org.wisc.business.service.DuplicateUserNameException;
import org.wisc.business.service.UserService;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/v1/users")
public class UserController {
    @Resource
    UserService userService;

    // TODO user auth
    @PostMapping("")
    public @ResponseBody
    AjaxResponse addUser(@RequestBody User user) {
        // DEBUGGING
        System.out.println("POST v1/users/");
        System.out.println(user);
        User newUser;
        if (user.getUsername() == null || user.getUsername().length() < 6)
            return AjaxResponse.error(400,
                    "Invalid username" + user.getUsername());
        if (user.getEmail() == null || user.getEmail().length() < 6)
            return AjaxResponse.error(400, "Invalid email("+user.getEmail()+
                    ")");
        try {
            newUser = userService.add(user);
        } catch (DuplicateUserNameException dune) {
            return AjaxResponse.error(400,
                    "Duplicate username(" + user.getUsername()+")");
        } catch (DuplicateEmailException dee) {
            return AjaxResponse.error(400,
                    "Duplicate email("+user.getEmail()+")");
        }
        return (newUser == null?AjaxResponse.error(400,
                "User("+user.getId()+") already exists."):
                AjaxResponse.success(newUser));
    }

    // TODO user auth
    @PutMapping("")
    public @ResponseBody AjaxResponse updateUser(@RequestBody User user) {
        User newUser = null;
        try {
            newUser = userService.update(user);
        } catch (DuplicateUserNameException dune) {
            return AjaxResponse.error(400,
                    "Duplicate username(" + user.getUsername()+")");
        } catch (DuplicateEmailException dee) {
            return AjaxResponse.error(400,
                    "Duplicate email("+user.getEmail()+")");
        }
        if (newUser == null) {
            return AjaxResponse.error(400, "User("+user.getId()+") is " +
                    "invalid.");
        }
        return AjaxResponse.success(newUser);
    }

    @GetMapping("")
    public @ResponseBody AjaxResponse getAllUsers() {
        return AjaxResponse.success(userService.all());
    }

    @GetMapping("/{id}")
    public @ResponseBody AjaxResponse getUser(@PathVariable String id) {
        User user = userService.findById(id);
        if (user == null) {
            return AjaxResponse.error(400, "Invalid user id(" + id + ")");
        }
        return AjaxResponse.success(user);
    }

    @GetMapping("/username/{username}")
    public @ResponseBody AjaxResponse getCourseByUserName(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return AjaxResponse.error(400, "Invalid username(" + username + ")");
        }
        return AjaxResponse.success(user);
    }

    @GetMapping("/email/{email}")
    public @ResponseBody AjaxResponse getCourseByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return AjaxResponse.error(400, "Invalid email(" + email + ")");
        }
        return AjaxResponse.success(user);
    }

    @GetMapping("/name/{name}")
    public @ResponseBody AjaxResponse getCourseByName(@PathVariable String name) {
        return AjaxResponse.success(userService.findByName(name));
    }

    // TODO user auth
    @DeleteMapping("")
    public @ResponseBody
    AjaxResponse deleteUser(@RequestBody User user) {
        if (userService.delete(user))
            return AjaxResponse.success();
        return AjaxResponse.error(400, "Invalid user("+user.getId()+")");
    }
}
