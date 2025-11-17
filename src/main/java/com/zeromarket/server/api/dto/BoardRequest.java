package com.zeromarket.server.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRequest extends DefaultRequest {

    private Long id;
    private String title;
    private String content;
    private Long writerId;
    private String status;

}
