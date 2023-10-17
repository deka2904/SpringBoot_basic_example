package com.mysit.sbb.category;

import com.mysit.sbb.DataNotFoundException;
import com.mysit.sbb.answer.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
