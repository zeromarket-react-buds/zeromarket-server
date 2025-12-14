package com.zeromarket.server.api.dto.product;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateRequest {
    private Long sellerId;
    private String productTitle;
    private Long categoryDepth1;
    private Long categoryDepth2;
    private Long categoryDepth3;
    private Long sellPrice;
    private String productDescription;
    private String productStatus;
    private String salesStatus;
    private boolean direct;
    private boolean delivery;
    private String sellingArea;

    private Long productId; //자동생성값
//    public Long getProductId() { //DB insert 후 자동생성 id 받을때
//        return productId;
//    }
    private List<ProductImageDto> images;//첨부 이미지 객체 형태,내부클래스public static

    // vision 저장용
    private String aiCaption;
    private String aiTags;

    //위치정보
    private Long referenceAreaId;
    private Double latitude;
    private Double longitude;
    private String roadAddress;
    private String jibunAddress;
    private String buildingName;
    private String zipCode;
    private String legalDongCode;
    private String adminDongCode;
    private String adminDongName;
    private ProductLocationDto location;

    @Getter
    @Setter
    public static class ProductLocationDto {
        private String locationName;  // 장소 이름
        private Long referenceAreaId;
        private Double latitude;
        private Double longitude;
        private String roadAddress;
        private String jibunAddress;
        private String buildingName;
        private String zipCode;
        private String legalDongCode;
        private String adminDongCode;
        private String adminDongName;
    }

    @Getter
    @Setter
    public static class ProductImageDto {
        private String imageUrl; //supabase에서 받아온 이미지 url 리스트
        private Integer sortOrder;
        private Boolean isMain; //프론트에서 보낸 대표이미지
    }

}
