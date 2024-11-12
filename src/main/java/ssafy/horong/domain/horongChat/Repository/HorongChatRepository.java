package ssafy.horong.domain.horongChat.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.horongChat.entity.HorongChat;

import java.util.List;

public interface HorongChatRepository extends JpaRepository<HorongChat, Long> {
    List<HorongChat> findByUser_Id(Long userId);

    List<HorongChat> findByRoomId(Long roomId);

}
