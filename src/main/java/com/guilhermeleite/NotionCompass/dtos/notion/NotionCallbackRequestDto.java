package com.guilhermeleite.NotionCompass.dtos.notion;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotionCallbackRequestDto {
    @NotBlank(message = "Code must not be blank")
    private String code;
    private String state;
}
