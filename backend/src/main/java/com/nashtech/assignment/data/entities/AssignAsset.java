package com.nashtech.assignment.data.entities;

import com.nashtech.assignment.data.constants.EAssignStatus;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "assign_asset_tbl")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "assigned_date")
    private Date assignedDate;
    @Column(name = "note", columnDefinition = "text")
    private String note;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EAssignStatus status;
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @OneToOne(mappedBy = "assignAsset")
    private ReturnAsset returnAsset;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "assigned_to_user_id")
    private User userAssignedTo;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "assigned_by_user_id")
    private User userAssignedBy;
}
