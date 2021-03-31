package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.BidStatus;
import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.services.BidRepository;
import org.pikIt.studentInit.services.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("hasAuthority('EXPERT')")
public class ExpertController {
    private VotingRepository votingRepository;
    private BidRepository bidRepository;
    private final Integer VOTES_FOR = 200;
    private final Integer VOTES_AGAINST = 200;

    @GetMapping("users/expertPage")
    public String main(Model model,@AuthenticationPrincipal User user){
        model.addAttribute("message", "Все доступные заявки");
        model.addAttribute("bids", bidRepository.findByStatus(BidStatus.Голосование_эксперт_состав));
        model.addAttribute("user", user);
        return "users/expertPage";
    }

    @Autowired
    public void setBidRepository(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("votes/votingStud/{bid}")
    public String studentVoting(Model model, @PathVariable Bid bid) {
        model.addAttribute("bidStudVote", bid);
        return "votes/votingStud";
    }

    @GetMapping("votes/votingExpert/{bid}")
    public String expertVoting(Model model, @PathVariable Bid bid) {
        model.addAttribute("bidExpertVote", bid);
        return "votes/votingExpert";
    }

    @PostMapping("users/expertPage/replaceBids")
    public String replaceBids(Model model, @AuthenticationPrincipal User user){
        main(model, user);
        return "users/expertPage";
    }
    @PostMapping("users/expertPage/filterText")
    public String filterText(@RequestParam String filterText,
                             Model model) {
        BidController.searchByText(filterText, model, bidRepository);
        return "users/expertPage";
    }

    @PostMapping("users/expertPage/allBidsByName")
    public String allBidsByName(
            @AuthenticationPrincipal User user,
            Model model) {
        UserController.allBidsByName(model,bidRepository,user);
        return "users/expertPage";
    }
    @PostMapping("users/expertPage/expertVoteFor")
    public String expertVotingFor(@AuthenticationPrincipal User user, @RequestParam boolean yes, @RequestParam Bid bid) {
        UserController.votingFor(user, yes, bid, votingRepository, VOTES_FOR);
        return "redirect:/users/expertPage";
    }
    @PostMapping("users/expertPage/expertVoteAgainst")
    public String expertVotingAgainst(@AuthenticationPrincipal User user, @RequestParam boolean no, @RequestParam Bid bid) {
        UserController.votingAgainst(user, no, bid, votingRepository, VOTES_AGAINST);
        return "redirect:/users/expertPage";
    }

    @Autowired
    public void setVotingRepository(VotingRepository votingRepository) {
        this.votingRepository = votingRepository;
    }
}
