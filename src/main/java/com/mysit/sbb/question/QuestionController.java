package com.mysit.sbb.question;

import com.mysit.sbb.answer.Answer;
import com.mysit.sbb.answer.AnswerService;
import com.mysit.sbb.category.Category;
import com.mysit.sbb.comment.Comment;
import com.mysit.sbb.comment.CommentForm;
import com.mysit.sbb.comment.CommentService;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import com.mysit.sbb.answer.AnswerForm;
import org.springframework.data.domain.Page;
import java.security.Principal;
import com.mysit.sbb.user.SiteUser;
import com.mysit.sbb.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {
    private final QuestionService questionService;
    private final UserService userService;
    private final CommentService commentService;
    private final AnswerService answerService;
    private final QuestionRepository questionRepository;

    @GetMapping("/categorylist/{id}")
    public String categorylist(Model model, @RequestParam(value="page", defaultValue="0") int page, @RequestParam(value = "kw", defaultValue = "") String kw, @PathVariable("id") Integer id) {
        if (id.equals(0)){
            Page<Question> paging = this.questionService.getList(page, kw);
            model.addAttribute("paging", paging);
            model.addAttribute("kw", kw);
            model.addAttribute("id", 0);
        }else{
            Page<Question> paging = this.questionService.getCategoryList(page, id);
            model.addAttribute("paging", paging);
            model.addAttribute("id", id);
        }
        return "question_list";
    }

    @GetMapping(value = "/detailviewup/{id}")
    public String detailviewup(Model model, @PathVariable("id") Integer id, AnswerForm answerForm,
                               @RequestParam(value = "answerPage", defaultValue = "0") int answerPage,
                               @RequestParam(value = "commentPage", defaultValue = "0") int commentPage) {
        Question question = this.questionService.getQuestion(id);
        Page<Answer> answerPaging =  this.answerService.getList(question, answerPage);
        Page<Comment> commentPaging = this.commentService.getList(question, commentPage);
//        Page<Comment> Answer_commentPaging = this.commentService.getList(question.getAnswerList(),commentPage);
        this.questionService.setQuestionViewUp(question);
        model.addAttribute("question", question);
        model.addAttribute("answerPaging", answerPaging);
        model.addAttribute("commentPaging", commentPaging);
//        model.addAttribute("Answer_commentPaging", Answer_commentPaging);
        return "question_detail";
    }
    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm,
                         @RequestParam(value = "answerPage", defaultValue = "0") int answerPage,
                         @RequestParam(value = "commentPage", defaultValue = "0") int commentPage) {
        Question question = this.questionService.getQuestion(id);
        Page<Answer> answerPaging =  this.answerService.getList(question, answerPage);
        Page<Comment> commentPaging = this.commentService.getList(question, commentPage);
//        Page<Comment> Answer_commentPaging = this.commentService.getList(question.getAnswerList(),commentPage);
        model.addAttribute("question", question);
        model.addAttribute("answerPaging", answerPaging);
        model.addAttribute("commentPaging", commentPaging);
//        model.addAttribute("Answer_commentPaging", Answer_commentPaging);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser, questionForm.getCategory());
        return "redirect:/question/categorylist/0"; // 질문 저장후 질문목록으로 이동
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent(), questionForm.getCategory());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/create/question/{id}")
    public String createQuestionComment(CommentForm commentForm) {
        return "comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create/question/{id}")
    public String createQuestionComment(Model model, @PathVariable("id") Integer id, @Valid CommentForm commentForm,
                                        BindingResult bindingResult, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser user = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
        Comment comment = this.commentService.create(question, commentForm.getContent(), user);
        model.addAttribute("question", question);
        return String.format("redirect:/question/detail/%s#comment_%s", comment.getQuestion().getId(), comment.getId());
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/modify/{id}")
    public String questionCommentModify(CommentForm commentForm, @PathVariable("id") Integer id, Principal principal) {
        Comment comment = this.commentService.getComment(id);
        if(!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        commentForm.setContent(comment.getContent());
        return "comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/modify/{id}")
    public String questionCommentModify(@Valid CommentForm commentForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "comment_form";
        }
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.commentService.modify(comment, commentForm.getContent());
        return String.format("redirect:/question/detail/%s", comment.getQuestion().getId());
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/delete/{id}")
    public String questionCommentDelete(Principal principal, @PathVariable("id") Integer id) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/question/detail/%s", comment.getQuestion().getId());
    }
}
