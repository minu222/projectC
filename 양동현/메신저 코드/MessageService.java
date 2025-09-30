package dwacademy.mylms001.service;

import dwacademy.mylms001.entity.Message;
import dwacademy.mylms001.repository.MessageDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageDao dao;

    public List<Message> inbox(Long userId) {
        return dao.findInbox(userId);
    }

    public List<Message> sent(Long userId) {
        return dao.findSent(userId);
    }

    public Long send(Long senderId, Long receiverId, String content) {
        return dao.save(senderId, receiverId, content);
    }

    public void markRead(Long id, boolean read) {
        dao.markRead(id, read);
    }

    public void delete(Long id) {
        dao.delete(id);
    }

    public Long findUserIdByNickname(String nickname) {
        return dao.findUserIdByNickname(nickname);
    }
}
