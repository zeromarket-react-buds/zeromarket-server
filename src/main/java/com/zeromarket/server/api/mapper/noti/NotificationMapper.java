package com.zeromarket.server.api.mapper.noti;

import com.zeromarket.server.api.dto.noti.NotificationDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationMapper {


    List<NotificationDto> findNotificationsByMember(@Param("memberId") Long memberId,
                                       @Param("size") int size);

    int countUnreadByMember(@Param("memberId") Long memberId);

    int markAsRead(@Param("memberId") Long memberId,
                   @Param("notificationId") Long notificationId);

    int markChatRoomAsRead(@Param("memberId") Long memberId,
                           @Param("chatRoomId") Long chatRoomId);

    int upsertChatNotification(@Param("memberId") Long memberId,
                               @Param("chatRoomId") Long chatRoomId,
                               @Param("body") String body,
                               @Param("linkUrl") String linkUrl);
}
