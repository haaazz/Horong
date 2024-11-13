package ssafy.horong.domain.horongChat.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.horongChat.entity.HorongChat;

public interface HorongChatRepository extends JpaRepository<HorongChat, Long> {

}
