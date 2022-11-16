package com.nashtech.assignment.data.entities;

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

    @Column(name = "assigned_date")
    private Date returnedDate;
    @Column(name = "status")
    private EReturnStatus status;
    @Column(name = "is_deleted")
    private boolean isDeleted;
}
