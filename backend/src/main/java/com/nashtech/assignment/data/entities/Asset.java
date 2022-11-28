package com.nashtech.assignment.data.entities;

import com.nashtech.assignment.data.constants.EAssetStatus;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

    @Column(name = "asset_name")
    private String name;
    @Column(name = "asset_code")
    private String assetCode;
    @Column(name = "installed_date")
    private Date installedDate;
    @Column(name = "specification", columnDefinition = "text")
    private String specification;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EAssetStatus status;
    @Column(name = "location")
    private String location;
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "asset", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AssignAsset> assignAssets;

    @OneToMany(mappedBy = "asset", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ReturnAsset> returnAssets;
}
