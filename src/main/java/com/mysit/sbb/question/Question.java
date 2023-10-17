package com.mysit.sbb.question;
import java.time.LocalDateTime;
import java.util.List;

import com.mysit.sbb.answer.Answer;
import com.mysit.sbb.category.Category;
import com.mysit.sbb.comment.Comment;
import jakarta.persistence.*;
import com.mysit.sbb.user.SiteUser;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

    @OneToMany(mappedBy = "question")
    private List<Comment> commentList;

    @ManyToOne
    private Category category;
}