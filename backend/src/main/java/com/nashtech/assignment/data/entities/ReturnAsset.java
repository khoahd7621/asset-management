package com.nashtech.assignment.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nashtech.assignment.data.constants.EReturnStatus;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "return_asset_tbl")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "returned_date")
    private Date returnedDate;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EReturnStatus status;
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "asset_id")
    @JsonIgnore
    private Asset asset;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "assign_asset_id", referencedColumnName = "id")
    @JsonIgnore
    private AssignAsset assignAsset;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "requested_user_id")
    @JsonIgnore
    private User userRequestedReturn;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "accepted_user_id")
    @JsonIgnore
    private User userAcceptedReturn;
}
