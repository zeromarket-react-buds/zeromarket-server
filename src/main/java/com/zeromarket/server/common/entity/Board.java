package com.zeromarket.server.common.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Board {

    private Long id;
    private String title;
    private String content;
    private Long writerId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
