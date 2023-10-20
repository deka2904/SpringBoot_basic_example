package com.mysit.sbb.category;

import com.mysit.sbb.DataNotFoundException;
import com.mysit.sbb.answer.Answer;
import com.mysit.sbb.comment.CommentService;
import com.mysit.sbb.question.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public Category getCategory(Integer id) {
        Optional<Category> category = this.categoryRepository.findById(id);

        if (category.isPresent()) {
            return category.get();
        } else {
            throw new DataNotFoundException("category not found");
        }
    }
}
