package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.domain.UserRepository;
import com.example.demo.dto.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    UserRepository userRepository;

    @Autowired
    HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        String id;
        String name;
        String email;
        String gender;
        String age;
        String birthday;
        String birthyear;
        String profileImage;
        String mobile;
        Map<String, Object> response = oAuth2User.getAttributes();

        if (registrationId.equals("naver")) {
            Map<String, Object> hash = (Map<String, Object>) response.get("response");

            id = (String) hash.get("id");
            name = (String) hash.get("name");
            email = (String) hash.get("email");
            gender = (String) hash.get("gender");
            age = (String) hash.get("age");
            birthyear = (String) hash.get("birthyear");
            birthday = (String) hash.get("birthday");
            profileImage = (String) hash.get("profile_image");
            mobile = (String) hash.get("mobile");

        } else {
            throw new OAuth2AuthenticationException("허용되지 않는 인증입니다.");
        }

        User user;
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        }else {
            user = User.builder()
                    .id(id)
                    .name(name)
                    .email(email)
                    .gender(gender)
                    .age(age)
                    .birthyear(birthyear)
                    .birthday(birthday)
                    .profileImage(profileImage)
                    .role(Role.ROLE_USER)
                    .mobile(mobile)
                    .build();

            userRepository.save(user);
        }

        httpSession.setAttribute("user", user);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString())),
                oAuth2User.getAttributes(),
                userNameAttributeName
        );
    }
}
