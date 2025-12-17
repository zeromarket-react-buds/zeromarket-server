// 찜상태 변경: 찜 등록, 해제, soft delete 적용/복원, 찜토글 기능
package com.zeromarket.server.api.service.mypage;

public interface WishCommandService {
    //  찜 추가 (POST)
    boolean addWish(Long memberId, Long productId);

    //  찜 삭제 (DELETE)
    boolean deleteWish(Long memberId, Long productId);

    //  필요하면 유지 (찜목록 내부 토글용으로만 사용)
    boolean toggleWish(Long memberId, Long productId);
}
