package com.chattingo.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.chattingo.Exception.ChatException;
import com.chattingo.Exception.MessageException;
import com.chattingo.Exception.UserException;
import com.chattingo.Model.Chat;
import com.chattingo.Model.Message;
import com.chattingo.Model.User;
import com.chattingo.Payload.SendMessageRequest;
import com.chattingo.Repository.MessageRepository;
import com.chattingo.Service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ChatServiceImpl chatService;

     @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public Message sendMessage(SendMessageRequest req) throws UserException, ChatException {
        User user = this.userService.findUserById(req.getUserId());
        Chat chat = this.chatService.findChatById(req.getChatId());

        Message message = new Message();
        message.setChat(chat);
        message.setUser(user);
        message.setContent(req.getContent());
        message.setTimestamp(LocalDateTime.now());

        message = this.messageRepository.save(message);

        // TODO: Fix WebSocket messaging - temporarily disabled to prevent 500 errors
        try {
            // Send message to WebSocket topic based on chat type
            if (chat.isGroup()) {
                messagingTemplate.convertAndSend("/group/" + chat.getId(), message);
            } else {
                messagingTemplate.convertAndSend("/user/" + chat.getId(), message);
            }
        } catch (Exception e) {
            // Log the error but don't fail the message creation
            System.err.println("WebSocket messaging failed: " + e.getMessage());
        }

        return message;
    }

    @Override
    public List<Message> getChatsMessages(Integer chatId, User reqUser) throws ChatException, UserException {

        Chat chat = this.chatService.findChatById(chatId);

        if (!chat.getUsers().contains(reqUser)) {
            throw new UserException("You are not related to this chat");
        }

        List<Message> messages = this.messageRepository.findByChatId(chat.getId());

        return messages;

    }

    @Override
    public Message findMessageById(Integer messageId) throws MessageException {
        Message message = this.messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException("The required message is not found"));
        return message;
    }

    @Override
    public void deleteMessage(Integer messageId, User reqUser) throws MessageException {
        Message message = this.messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException("The required message is not found"));

        if (message.getUser().getId() == reqUser.getId()) {
            this.messageRepository.delete(message);
        } else {
            throw new MessageException("You are not authorized for this task");
        }
    }

}
