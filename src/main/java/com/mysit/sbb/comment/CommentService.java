package com.mysit.sbb.comment;


import com.mysit.sbb.DataNotFoundException;
import com.mysit.sbb.answer.Answer;
import com.mysit.sbb.question.Question;
import com.mysit.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment create(Question question, String content, SiteUser author){
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setQuestion(question);
        comment.setAuthor(author);
        comment = this.commentRepository.save(comment);
        return comment;
    }

    public Comment create(Answer answer, String content, SiteUser author){
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setAnswer(answer);
        comment.setAuthor(author);
        comment = this.commentRepository.save(comment);
        return comment;
    }

    public Comment getComment(Integer id) {
        Optional<Comment> comment = this.commentRepository.findById(id);
        if (comment.isPresent()) {
            return comment.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }
    public void modify(Comment comment, String content) {
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }
    public void delete(Comment comment) {
        this.commentRepository.delete(comment);
    }
    @Transactional
    public void deleteCommentsByAnswer(Answer answer) {
        commentRepository.deleteCommentsByAnswer(answer);
    }
    public Page<Comment> getList(Question question, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 2, Sort.by(sorts));
        return this.commentRepository.findAllByQuestion(question, pageable);
    }
    public List<Page<Comment>> getList(List<Answer> answers, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 2, Sort.by(sorts));
        List<Page<Comment>> commentPageList = new ArrayList<>();
        for (int i = 0; i < answers.size(); i++) {
            commentPageList.add(this.commentRepository.findAllByAnswer(answers.get(i), pageable));
        }
        return commentPageList;
    }

    public Page<Comment> getList(Answer answer, int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 2, Sort.by(sorts));
        return this.commentRepository.findAllByAnswer(answer, pageable);
    }
}
