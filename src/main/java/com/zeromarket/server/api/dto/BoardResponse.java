package com.zeromarket.server.api.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardResponse {

    private String id;
    private String title;
    private String content;
    private String writerId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
