package com.zeromarket.server.api.dto.mypage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikedSellerResponse {

    private Long sellerId; //셀러샵 페이지 이동(/sellershop/{sellerId})
    private String nickname; //찜목록 셀러 이름 표시
    private String profileImage; //프로필 이미지
    private boolean liked; //하트 상태 (프론트 공통 컴포넌트 재사용)

}
