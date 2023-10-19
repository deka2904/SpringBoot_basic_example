package com.mysit.sbb.user;

import com.mysit.sbb.CommonUtil;
import com.mysit.sbb.EmailException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import com.mysit.sbb.DataNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TempPasswordMail tempPasswordMail;
    private final CommonUtil commonUtil;

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }
    @Transactional
    public void updatePassword(String username, String newPassword) {
        SiteUser user = userRepository.findByUsername(username);

        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new DataNotFoundException("사용자를 찾을 수 없습니다.");
        }
    }
    @Transactional
    public void modifyPassword(String email) throws EmailException {
        SiteUser user = userRepository.findByemail(email).orElse(null);
        if (user == null) {
            throw new DataNotFoundException("해당 이메일의 유저가 없습니다.");
        }
        String tempPassword = commonUtil.createTempPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);
        tempPasswordMail.sendSimpleMessage(email, tempPassword);
    }
}