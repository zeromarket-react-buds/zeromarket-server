package com.zeromarket.server.api.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateRequest {
//    private Long productId; //url로 id 보내니까 불필요
    private String productTitle;
    private Long categoryDepth1;
    private Long categoryDepth2;
    private Long categoryDepth3;
    private Long sellPrice;
    private String productDescription;
    private String productStatus;
//    private String salesStatus;
    private boolean direct;
    private boolean delivery;
    private String sellingArea;

    //이미지 수정
    private List<ImageDto> images;

    @Getter
    @Setter
     public static class ImageDto{
        private Long imageId; //기존이미지는 존재, 새이미지는null
        private String imageUrl;
        @JsonProperty("isMain")
        private Boolean isMain;
        private Integer sortOrder;

     }

}
