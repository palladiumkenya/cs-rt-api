package org.kenyahmis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.kenyahmis.validator.ValidDate;


@Schema(name = "Client", description = "Client demographics", implementation = ClientDto.class)
@Data
public class ClientDto {
    @NotBlank
    private String county;
    @NotBlank
    private String subCounty;
    @NotBlank
    private String ward;
    @NotBlank
    @Schema(description = "Patient facility identifier", example = "185")
    private String patientPk;
    @NotBlank
    private String sex;
    @ValidDate
    @Schema(example = "1990-11-11")
    private String dob;
}
