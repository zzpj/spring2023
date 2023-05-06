package com.zzpj.EventManager.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventErrorResponse {

    private LocalDateTime localDateTime;
    private Integer status;
    private List<String> errors;
}
