package com.mysit.sbb.comment;

import com.mysit.sbb.answer.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.answer = :answer")
    void deleteCommentsByAnswer(@Param("answer") Answer answer);
}
