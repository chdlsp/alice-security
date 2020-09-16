package com.chdlsp.alice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @NotNull
    private String email;

    @Setter
    @NotNull
    private String name;

    private String password;

    //    // UserLoginHistory N : 1 User
    //    @ManyToOne
    //    private User user;

}
