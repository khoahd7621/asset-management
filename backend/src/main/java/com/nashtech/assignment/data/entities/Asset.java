package com.nashtech.assignment.data.entities;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.ELocation;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "asset_tbl")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "asset_code")
    private String assetCode;
    @Column(name = "installed_date")
    private Date installedDate;
    @Column(name = "specification", columnDefinition = "text")
    private String specification;
    @Column(name = "status")
    private EAssetStatus status;
    @Column(name = "location")
    private ELocation location;
    @Column(name = "is_deleted")
    private boolean isDeleted;
}
