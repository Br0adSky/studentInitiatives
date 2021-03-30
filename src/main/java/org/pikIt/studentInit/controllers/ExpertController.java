package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.BidStatus;
import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.services.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasAuthority('EXPERT')")
public class ExpertController {
    private VotingRepository votingRepository;
    private Integer VOTES_FOR = 200;
    private Integer VOTES_AGAINST = 200;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("votes/votingStud/{bid}")
    public String studentVoting(Model model, @PathVariable Bid bid) {
        model.addAttribute("bidStudVote", bid);
        return "votes/votingStud";
    }

    public String expertVotingFor(@AuthenticationPrincipal User user, @RequestParam boolean yes, @RequestParam Bid bid) {
        if(bid.getStatus() == BidStatus.Голосование_эксперт_состав)
            UserController.votingFor(user, yes, bid, votingRepository, VOTES_FOR);
        return "";
    }

    public String expertVotingAgainst(@AuthenticationPrincipal User user, @RequestParam boolean no, @RequestParam Bid bid) {
        if(bid.getStatus() == BidStatus.Голосование_эксперт_состав)
            UserController.votingAgainst(user, no, bid, votingRepository, VOTES_AGAINST);
        return "";
    }

    @Autowired
    public void setVotingRepository(VotingRepository votingRepository) {
        this.votingRepository = votingRepository;
    }
//    @GetMapping("")
//    public String expertVoting(Model model, @RequestParam boolean yes, @RequestParam boolean no){
//        return "";
//    }
}
