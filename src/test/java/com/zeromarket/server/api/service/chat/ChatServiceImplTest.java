//package com.zeromarket.server.api.service.chat;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//import com.zeromarket.server.api.dto.chat.ChatMessageResponse;
//import com.zeromarket.server.api.mapper.chat.ChatMapper;
//import com.zeromarket.server.common.exception.ApiException;
//import java.util.List;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//
//@SpringBootTest
//@Slf4j
//@Transactional // 테스트 후 롤백
//public class ChatServiceImplTest {
//
//    @Autowired
//    ChatService chatService;
//
//    @Autowired
//    ChatMapper chatMapper;
//
//    @Test
//    void 있는_구매자와_있는_상품으로_채팅방_만들기() {
//        // given
//        Long productId = 3L;
//        Long buyerId = 3L;
//
//        // when
//        Long chatRoomId = chatService.selectChatRoomByProductIdBuyerId(productId, buyerId);
//
//        // then
//        log.info("chatRoomId: {}", chatRoomId);
//        assertThat(chatRoomId).isNotNull();
//        assertThat(chatRoomId).isGreaterThan(0L);
//    }
//
//    @Test
//    void 이미_존재하는_채팅방_조회() {
//        // given
//        Long productId = 3L;
//        Long buyerId = 3L;
//        Long firstChatRoomId = chatService.selectChatRoomByProductIdBuyerId(productId, buyerId);
//
//        // when - 같은 상품, 같은 구매자로 다시 요청
//        Long secondChatRoomId = chatService.selectChatRoomByProductIdBuyerId(productId, buyerId);
//
//        // then - 같은 채팅방 ID 반환
//        log.info("첫 번째 chatRoomId: {}, 두 번째 chatRoomId: {}", firstChatRoomId, secondChatRoomId);
//        assertThat(firstChatRoomId).isEqualTo(secondChatRoomId);
//    }
//
//    @Test
//    void 새로운_채팅방_생성시_참여자_2명_생성() {
//        // given
//        Long productId = 3L;
//        Long buyerId = 4L; // 기존에 채팅방 없는 새로운 구매자
//
//        // when
//        Long chatRoomId = chatService.selectChatRoomByProductIdBuyerId(productId, buyerId);
//
//        // then
//        log.info("생성된 chatRoomId: {}", chatRoomId);
//        assertThat(chatRoomId).isNotNull();
//
//        // 참여자 2명(구매자, 판매자) 확인
//        int participantCount = chatMapper.countChatParticipants(chatRoomId);
//        assertThat(participantCount).isEqualTo(2);
//    }
//
//    @Test
//    void 새로운_채팅방_생성시_초기_메시지_생성() {
//        // given
//        Long productId = 3L;
//        Long buyerId = 18L; // 새로운 구매자
//
//        // when
//        Long chatRoomId = chatService.selectChatRoomByProductIdBuyerId(productId, buyerId);
//
//        // then
//        log.info("생성된 chatRoomId: {}", chatRoomId);
//
//        // 초기 메시지 확인
//        List<ChatMessageResponse> messages = chatMapper.selectChatMessages(chatRoomId, buyerId);
//        assertThat(messages).isNotEmpty();
//        assertThat(messages.get(0).getContent()).isEqualTo("구매 의사 있어요.");
//        assertThat(messages.get(0).getMemberId()).isEqualTo(buyerId);
//    }
//
//    @Test
//    void 존재하지_않는_상품으로_채팅방_만들기_실패() {
//        // given
//        Long invalidProductId = 99999L;
//        Long buyerId = 3L;
//
//        // when & then
//        assertThatThrownBy(() ->
//            chatService.selectChatRoomByProductIdBuyerId(invalidProductId, buyerId))
//            .isInstanceOf(ApiException.class)
//            .hasMessageContaining("상품 정보를 찾을 수 없습니다");
//    }
//
////    @Test
////    void 삭제된_상품으로_채팅방_만들기_실패() {
////        // given
////        Long deletedProductId = 100L; // is_deleted = true인 상품
////        Long buyerId = 3L;
////
////        // when & then
////        assertThatThrownBy(() ->
////            chatService.selectChatRoomByProductIdBuyerId(deletedProductId, buyerId))
////            .isInstanceOf(ApiException.class)
////            .hasMessageContaining("PRODUCT_NOT_FOUND");
////    }
//
////    @Test
////    void 숨김_상품으로_채팅방_만들기_실패() {
////        // given
////        Long hiddenProductId = 101L; // is_hidden = true인 상품
////        Long buyerId = 3L;
////
////        // when & then
////        assertThatThrownBy(() ->
////            chatService.selectChatRoomByProductIdBuyerId(hiddenProductId, buyerId))
////            .isInstanceOf(ApiException.class)
////            .hasMessageContaining("PRODUCT_NOT_FOUND");
////    }
//}