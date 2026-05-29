package com.midasteknologi.e_kyc_verification_summary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PaginatedResponse extends BaseResponse {

    private Long totalElements;
    private Integer totalPages;
    private Integer page;
    private Integer size;
    private Object data;
}
