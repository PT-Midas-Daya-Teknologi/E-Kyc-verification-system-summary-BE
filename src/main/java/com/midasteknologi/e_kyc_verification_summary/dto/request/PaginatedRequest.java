package com.midasteknologi.e_kyc_verification_summary.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedRequest {

    @NotNull
    @PositiveOrZero
    private int page;

    @NotNull
    @PositiveOrZero
    private int size;
}
