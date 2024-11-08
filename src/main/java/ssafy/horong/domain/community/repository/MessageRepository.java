package ssafy.horong.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssafy.horong.domain.community.entity.Message;
import ssafy.horong.domain.member.entity.User;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("select m from Message m where m.chatRoom.id = :chatRoomId")
    List<Message> findAllByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    List<Message> findAllByUser(User user);
}
