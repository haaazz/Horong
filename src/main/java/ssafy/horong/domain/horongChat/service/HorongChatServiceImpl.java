package ssafy.horong.domain.horongChat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.api.horongChat.request.HorongChatContentRequest;
import ssafy.horong.api.horongChat.response.ChatContentResponse;
import ssafy.horong.api.horongChat.response.ChatListResponse;
import ssafy.horong.api.horongChat.response.ChatRoomResponse;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.horongChat.HorongChatRepository;
import ssafy.horong.domain.horongChat.command.SaveChatLogCommand;
import ssafy.horong.domain.horongChat.entity.HorongChat;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HorongChatServiceImpl implements HorongChatService {
    private final HorongChatRepository horongChatRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveChatLog(SaveChatLogCommand command) {
        // 요청마다 고유한 roomId를 생성
        Long uniqueRoomId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

        User currentUser = SecurityUtil.getLoginMemberId()
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new RuntimeException("User not found"));

        for (HorongChatContentRequest content : command.chatContents()) {
            HorongChat chatEntity = HorongChat.builder()
                    .content(content.content())
                    .authorType(content.authorType())
                    .user(currentUser)
                    .roomId(uniqueRoomId)
                    .build();

            horongChatRepository.save(chatEntity);
        }
    }

    public ChatListResponse getChatList() {
        List<HorongChat> chatList = horongChatRepository.findByUser_Id(
                SecurityUtil.getLoginMemberId().orElseThrow(() -> new RuntimeException("User not found"))
        );

        List<ChatRoomResponse> chatRoomResponses = chatList.stream()
                .collect(Collectors.groupingBy(HorongChat::getRoomId))
                .entrySet().stream()
                .map(entry -> new ChatRoomResponse(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(chat -> new ChatContentResponse(
                                        chat.getContent(),
                                        chat.getAuthorType(),
                                        chat.getCreatedAt()
                                ))
                                .toList()
                ))
                .toList();

        return new ChatListResponse(chatRoomResponses);
    }

    public ChatRoomResponse getChat(Long roomId) {
        List<HorongChat> chats = horongChatRepository.findByRoomId(roomId);

        List<ChatContentResponse> chatContentList = chats.stream()
                .map(chat -> new ChatContentResponse(
                        chat.getContent(),
                        chat.getAuthorType(),
                        chat.getCreatedAt()
                ))
                .toList();

        return new ChatRoomResponse(
                roomId,
                chatContentList
        );
    }
}
