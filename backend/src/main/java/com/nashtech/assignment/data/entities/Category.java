package com.nashtech.assignment.data.entities;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "category_tbl")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", unique = true)
    private String name;
    @Column(name = "prefix_asset_code", unique = true)
    private String prefixAssetCode;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    List<Asset> assets;
}
