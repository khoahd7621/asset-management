package com.nashtech.assignment.data.entities;

import com.nashtech.assignment.data.constants.EGender;
import com.nashtech.assignment.data.constants.EUserType;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user_tbl")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_name")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "staff_code", unique = true)
    private String staffCode;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private EGender gender;
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    @Column(name = "joined_date")
    private Date joinedDate;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private EUserType type;
    @Column(name = "location")
    private String location;
    @Column(name = "is_first_login")
    private boolean isFirstLogin;
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @OneToMany(mappedBy = "userAssignedTo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AssignAsset> yourBorrowed;

    @OneToMany(mappedBy = "userAssignedBy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AssignAsset> yourAssignedTo;

    @OneToMany(mappedBy = "userRequestedReturn", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ReturnAsset> yourRequestedReturn;

    @OneToMany(mappedBy = "userAcceptedReturn", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ReturnAsset> yourAcceptedReturn;
}
