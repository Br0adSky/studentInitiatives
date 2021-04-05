package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.Comment;
import org.pikIt.studentInit.model.Role;
import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.services.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/comments")
public class CommentController {
    private final CommentsService commentsService;

    @Autowired
    public CommentController(CommentsService commentsService) {
        this.commentsService = commentsService;
    }

    @GetMapping("/mainPage/{bid}")
    public String addNewComment(@AuthenticationPrincipal User user, Model model,
                                @PathVariable Bid bid) {
        return commentsService.createNewComment(model, bid, user);
    }

    @PostMapping("/mainPage/{bid}")
    public String send(@AuthenticationPrincipal User user,
                       @Valid Comment newComment, BindingResult bindingResult,
                       Model model, @PathVariable Bid bid) {
        return commentsService.send(newComment,bindingResult,model, user, bid);
    }

    @PostMapping("/mainPage/back")
    public String back(@AuthenticationPrincipal User user) {
        if (user.getRoles().contains(Role.MODERATOR))
            return "redirect:/bids/bidList";
        else
            return "redirect:/users/userPage";
    }

    @GetMapping("/mainPage/delete/{comment}")
    public String delete(@AuthenticationPrincipal User user, @PathVariable Comment comment){
        commentsService.delete(user, comment);
        return "redirect:/comments/mainPage/" + comment.getBid().getId();
    }
}
