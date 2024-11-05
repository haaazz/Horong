package ssafy.horong.domain.horongChat.service;


import ssafy.horong.api.horongChat.response.ChatListResponse;
import ssafy.horong.api.horongChat.response.ChatRoomResponse;
import ssafy.horong.domain.horongChat.command.SaveChatLogCommand;

public interface HorongChatService {
    void saveChatLog(SaveChatLogCommand command);
    ChatListResponse getChatList();
    ChatRoomResponse getChat(Long chatId);
}
