package com.nashtech.assignment.data.entities;

import com.nashtech.assignment.data.constants.EGender;
import com.nashtech.assignment.data.constants.ELocation;
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
    private EGender gender;
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    @Column(name = "joined_date")
    private Date joinedDate;
    @Column(name = "type")
    private EUserType type;
    @Column(name = "location")
    private ELocation location;
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AssignAsset> assignAssets;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ReturnAsset> returnAssets;
}
