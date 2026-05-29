package com.midasteknologi.e_kyc_verification_summary.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {

    @CreationTimestamp
    private String createdAt;

    @Builder.Default
    private String createdBy = "SYSTEM";

    @UpdateTimestamp
    private String updatedAt;

    @Builder.Default
    private String updatedBy = "SYSTEM";
}
