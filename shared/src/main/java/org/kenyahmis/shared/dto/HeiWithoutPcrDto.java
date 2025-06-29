package org.kenyahmis.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.kenyahmis.shared.validator.ValidTimestamp;

@Schema(name = "HeiWithoutPcr")
public record HeiWithoutPcrDto(
        @Schema(name = "mflCode", example = "14423")
        @NotBlank
        String mflCode,
        @NotNull
        String heiId,
        @Schema(name = "createdAt", example = "2023-11-10 00:00:00")
        @NotNull
        @ValidTimestamp
        String createdAt,
        @Schema(name = "updatedAt", example = "2023-11-10 00:00:00")
        @ValidTimestamp
        String updatedAt
) {
}
