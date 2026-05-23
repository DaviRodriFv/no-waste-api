package com.nowaste.backend.dto.request;

import com.nowaste.backend.domain.enums.StatusResiduo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PatchStatusDTO {

    @NotNull
    private StatusResiduo status;
}
