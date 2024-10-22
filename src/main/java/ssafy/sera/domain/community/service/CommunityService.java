package ssafy.sera.domain.community.service;

import ssafy.sera.domain.community.entity.Board;
import ssafy.sera.domain.community.entity.Comment;

import java.util.List;

public interface CommunityService {
    Board createBoard(Board board);
    Board updateBoard(Long id, Board board);
    void deleteBoard(Long id);
    Board getBoardById(Long id);
    List<Board> getAllBoards();
    Comment createComment(Long boardId, Comment comment);
    void deleteComment(Long commentId);
    List<Comment> getCommentsByBoardId(Long boardId);
}
