package com.midasteknologi.e_kyc_verification_summary.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_video")
public class UserVideo extends BaseEntity {

    @Id
    private UUID id;

    private String name;

    private String path;

    private Boolean isActive;
}
