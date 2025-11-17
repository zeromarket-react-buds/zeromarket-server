package com.zeromarket.server.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRequest extends DefaultRequest {

    private String title;
    private String content;
    private String writerId;
    private String status;

}
