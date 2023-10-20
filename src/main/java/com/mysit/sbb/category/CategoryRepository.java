package com.mysit.sbb.category;

import com.mysit.sbb.answer.Answer;
import com.mysit.sbb.comment.Comment;
import com.mysit.sbb.question.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
