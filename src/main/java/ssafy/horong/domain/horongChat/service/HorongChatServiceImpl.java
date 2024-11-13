package ssafy.horong.domain.horongChat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.api.horongChat.request.HorongChatContentRequest;
import ssafy.horong.api.horongChat.response.ChatContentResponse;
import ssafy.horong.api.horongChat.response.ChatListResponse;
import ssafy.horong.api.horongChat.response.ChatRoomResponse;
import ssafy.horong.common.exception.User.MemberNotFoundException;
import ssafy.horong.common.exception.horongChat.ChatroomNotAuthenticatedException;
import ssafy.horong.common.exception.security.NotAuthenticatedException;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.horongChat.Repository.HorongChatRepository;
import ssafy.horong.domain.horongChat.Repository.HorongChatRoomRepository;
import ssafy.horong.domain.horongChat.command.SaveChatLogCommand;
import ssafy.horong.domain.horongChat.entity.HorongChat;
import ssafy.horong.domain.horongChat.entity.HorongChatRoom;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HorongChatServiceImpl implements HorongChatService {
    private final HorongChatRepository horongChatRepository;
    private final UserRepository userRepository;
    private final HorongChatRoomRepository horongChatRoomRepository;

    @Transactional
    public void saveChatLog(SaveChatLogCommand command) {

        // 현재 로그인한 사용자 찾기
        User currentUser = SecurityUtil.getLoginMemberId()
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. 새로운 채팅방 생성
        HorongChatRoom chatRoom = HorongChatRoom.builder()
                .user(currentUser)  // 현재 사용자를 채팅방에 연결
                .build();

        horongChatRoomRepository.save(chatRoom);  // 채팅방 저장

        // 2. 명령 객체에서 채팅 내용을 가져와서 해당 방에 메시지 저장 (양방향 연관관계 설정 없이 직접 room 설정)
        for (HorongChatContentRequest content : command.chatContents()) {
            HorongChat chatEntity = HorongChat.builder()
                    .content(content.content())  // 메시지 내용 설정
                    .authorType(content.authorType())  // Enum 타입으로 authorType 설정
                    .room(chatRoom)  // 직접적으로 방과 연결 (양방향 연관관계 메서드 없이 처리)
                    .build();

            horongChatRepository.save(chatEntity);  // DB에 메시지 저장
        }
    }

    public ChatListResponse getChatList() {
        // 현재 로그인한 사용자를 찾음
        User currentUser = getCurrentLoggedInMember();

        // 1. 사용자가 속한 모든 채팅방 조회
        List<HorongChatRoom> chatRooms = horongChatRoomRepository.findByUser(currentUser);

        // 2. 각 채팅방에 속한 메시지들을 가져와서 ChatRoomResponse로 변환
        List<ChatRoomResponse> chatRoomResponses = chatRooms.stream()
                .map(room -> {
                    List<ChatContentResponse> chatContents = room.getChats().stream()
                            .map(chat -> new ChatContentResponse(
                                    chat.getContent(),
                                    chat.getAuthorType(),
                                    chat.getCreatedAt()
                            ))
                            .toList();

                    return new ChatRoomResponse(room.getId(), chatContents);
                })
                .toList();

        return new ChatListResponse(chatRoomResponses);
    }

    public ChatRoomResponse getChat(Long roomId) {

        HorongChatRoom chatRoom = horongChatRoomRepository.findById(roomId)
                .orElseThrow();

        if (chatRoom.getUser() != getCurrentLoggedInMember()) {
            throw new ChatroomNotAuthenticatedException();
        }

        // 2. 해당 방에 속한 모든 메시지를 ChatContentResponse로 변환
        List<ChatContentResponse> chatContentList = chatRoom.getChats().stream()
                .map(chat -> new ChatContentResponse(
                        chat.getContent(),
                        chat.getAuthorType(),
                        chat.getCreatedAt()
                ))
                .toList();

        return new ChatRoomResponse(roomId, chatContentList);  // 응답 반환
    }

    private User getCurrentLoggedInMember() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(NotAuthenticatedException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(MemberNotFoundException::new);

        if (user.isDeleted()) {
            throw new NotAuthenticatedException();
        }
        return user;
    }
}
