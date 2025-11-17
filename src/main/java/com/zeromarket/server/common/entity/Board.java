package com.zeromarket.server.common.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Board {

    private String id;
    private String title;
    private String content;
    private String writerId;
    private String status;
    private String createdAt;
    private String updatedAt;

}
