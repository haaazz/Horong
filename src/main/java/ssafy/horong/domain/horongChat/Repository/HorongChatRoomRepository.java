package ssafy.horong.domain.horongChat.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.horongChat.entity.HorongChatRoom;
import ssafy.horong.domain.member.entity.User;

import java.util.List;

public interface HorongChatRoomRepository extends JpaRepository<HorongChatRoom, Long> {
    List<HorongChatRoom> findByUser(User user);
}