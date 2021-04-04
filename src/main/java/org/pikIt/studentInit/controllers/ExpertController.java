package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.BidStatus;
import org.pikIt.studentInit.model.Role;
import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.services.BidRepository;
import org.pikIt.studentInit.services.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
@PreAuthorize("hasAuthority('EXPERT')")
public class ExpertController {
    private final Integer VOTES_FOR = 200;
    private final Integer VOTES_AGAINST = 200;
    private VotingRepository votingRepository;
    private BidRepository bidRepository;

    @GetMapping("users/expertPage")
    public String main(Model model, @AuthenticationPrincipal User user) {
        UserController.replaceBidsByStatus(model, bidRepository, BidStatus.Voting_expert);
        model.addAttribute("message", "Доступные голосования");
        model.addAttribute("user", user);
        return "users/expertPage";
    }

    @Autowired
    public void setBidRepository(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("votes/voting/{bid}")
    public String voting(Model model, @PathVariable Bid bid, @AuthenticationPrincipal User user) {
        if(user.getRoles().contains(Role.EXPERT) && bid.getStatus() == BidStatus.Voting_expert){
            model.addAttribute("bidExpertVote", bid);
            model.addAttribute("student", false);
        }
        else{
            model.addAttribute("bidStudVote", bid);
            model.addAttribute("student", true);
        }

        return "votes/voting";
    }

    @PostMapping("users/expertPage/replaceBids")
    public String replaceBids(Model model, @AuthenticationPrincipal User user) {
        main(model, user);
        return "users/expertPage";
    }

    @PostMapping("users/expertPage/filterText")
    public String filterText(@RequestParam String filterText,
                             Model model) {
        BidController.searchByText(filterText, model, bidRepository, Collections.singletonList(BidStatus.Voting_expert));
        return "users/expertPage";
    }

    @PostMapping("users/expertPage/allBidsByName")
    public String allBidsByName(
            @AuthenticationPrincipal User user,
            Model model) {
        UserController.allBidsByName(model, bidRepository, user);
        return "users/expertPage";
    }

    @PostMapping("users/expertPage/expertVote")
    public String expertVoting(@AuthenticationPrincipal User user, @RequestParam Bid bid,
                               @RequestParam(required = false) boolean yes, @RequestParam(required = false) boolean no) {
        UserController.votingFor(user, bid, votingRepository, VOTES_FOR, BidStatus.Working, yes, no, VOTES_AGAINST, bidRepository);
        return "redirect:/users/expertPage";
    }


    @Autowired
    public void setVotingRepository(VotingRepository votingRepository) {
        this.votingRepository = votingRepository;
    }
}
