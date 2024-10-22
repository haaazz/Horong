package ssafy.sera.domain.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.sera.domain.community.entity.Board;
import ssafy.sera.domain.community.entity.Comment;
import ssafy.sera.domain.community.repository.BoardRepository;
import ssafy.sera.domain.community.repository.CommentRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityServiceImpl implements CommunityService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Board createBoard(Board board) {
        log.info("게시글 작성: {}", board.getTitle());
        return boardRepository.save(board);
    }

    @Override
    @Transactional
    public Board updateBoard(Long id, Board board) {
        log.info("게시글 수정: {}", board.getTitle());
        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        existingBoard.setTitle(board.getTitle());
        existingBoard.setContent(board.getContent());
        existingBoard.setType(board.getType());
        existingBoard.setImages(board.getImages());

        return boardRepository.save(existingBoard);
    }

    @Override
    @Transactional
    public void deleteBoard(Long id) {
        log.info("게시글 삭제: {}", id);
        boardRepository.deleteById(id);
    }

    @Override
    public Board getBoardById(Long id) {
        log.info("게시글 조회: {}", id);
        return boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
    }

    @Override
    public List<Board> getAllBoards() {
        log.info("모든 게시글 조회");
        return boardRepository.findAll();
    }

    @Override
    @Transactional
    public Comment createComment(Long boardId, Comment comment) {
        log.info("댓글 작성: {}", comment.getContent());
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        comment.setBoard(board);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        log.info("댓글 삭제: {}", commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<Comment> getCommentsByBoardId(Long boardId) {
        log.info("게시글에 대한 댓글 조회: {}", boardId);
        return commentRepository.findAll().stream()
                .filter(comment -> comment.getBoard().getId().equals(boardId))
                .toList();
    }
}
