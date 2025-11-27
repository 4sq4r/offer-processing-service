package kz.offerprocessservice.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseDTO {

    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
