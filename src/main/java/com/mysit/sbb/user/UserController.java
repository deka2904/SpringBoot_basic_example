package com.mysit.sbb.user;

import com.mysit.sbb.DataNotFoundException;
import com.mysit.sbb.EmailException;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private static final String TEMP_PASSWORD_FORM = "temp_password_form";

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }
        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
        return "redirect:/";
    }
    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/searchPw")
    public String resetPassword(UserSearchPwForm userSearchPwForm){
        return "password_form";
    }
    @PostMapping("/searchPw")
    public String resetPassword(@Valid UserSearchPwForm userSearchPwForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "password_form";
        }

        if (!userSearchPwForm.getPassword2().equals(userSearchPwForm.getPassword3())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "password_form";
        }
        userService.updatePassword(userSearchPwForm.getUsername(), userSearchPwForm.getPassword2());
        return "login_form";
    }
    @GetMapping("/tempPassword")
    public String sendTempPassword(TempPasswordForm tempPasswordForm){
        return "email_form";
    }
    @PostMapping("/tempPassword")
    public String sendTempPassword(@Valid TempPasswordForm tempPasswordForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "login_form";
        }
        try {
            userService.modifyPassword(tempPasswordForm.getEmail());
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            bindingResult.reject("emailNotFound", e.getMessage());
            return "login_form";
        } catch (EmailException e) {
            e.printStackTrace();
            bindingResult.reject("sendEmailFail", e.getMessage());
            return "login_form";
        }
        return "login_form";
    }
}