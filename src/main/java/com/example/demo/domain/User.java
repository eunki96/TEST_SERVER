package com.example.demo.domain;

import com.example.demo.dto.Role;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User implements Serializable {

    @Id
    private String id;
    private String name;
    private String email;
    private String gender;
    private String age;
    private String birthyear;
    private String birthday;
    private String profileImage;
    private String mobile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
