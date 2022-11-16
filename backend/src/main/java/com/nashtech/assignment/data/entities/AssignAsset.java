package com.nashtech.assignment.data.entities;

import com.nashtech.assignment.data.constants.EAssignmentStatus;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "assigment_tbl")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assigment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "assigned_date")
    private Date assignedDate;
    @Column(name = "note", columnDefinition = "text")
    private String note;
    @Column(name = "status")
    private EAssignmentStatus status;
    @Column(name = "is_deleted")
    private boolean isDeleted;
}
