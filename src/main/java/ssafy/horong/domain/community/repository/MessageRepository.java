package ssafy.horong.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssafy.horong.domain.community.entity.ChatRoom;
import ssafy.horong.domain.community.entity.Message;
import ssafy.horong.domain.member.entity.User;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("select m from Message m where m.chatRoom.id = :chatRoomId")
    List<Message> findAllByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    List<Message> findAllByUser(User user);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chatRoom = :chatRoom AND m.isRead = false AND m.user <> :user")
    long countUnreadMessagesForOpponent(@Param("chatRoom") ChatRoom chatRoom, @Param("user") User user);

    @Query("SELECT CASE WHEN c.host.id = :myId THEN c.guest.id ELSE c.host.id END " +
            "FROM ChatRoom c WHERE c.id = :chatRoomId")
    Long findOpponentIdByChatRoomIdAndUserId(@Param("chatRoomId") Long chatRoomId, @Param("myId") Long myId);
}