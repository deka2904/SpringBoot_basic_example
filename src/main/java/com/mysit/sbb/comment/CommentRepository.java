package com.mysit.sbb.comment;

import com.mysit.sbb.answer.Answer;
import com.mysit.sbb.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findAllByQuestion(Question question, Pageable pagealbe);   // 질문 하나당 답변 페이징
    Page<Comment> findAllByAnswer(List<Answer> answers, Pageable pageable);
    Page<Comment> findAllByAnswer(Answer answer, Pageable pageable);


    @Transactional
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.answer = :answer")
    void deleteCommentsByAnswer(@Param("answer") Answer answer);
}
