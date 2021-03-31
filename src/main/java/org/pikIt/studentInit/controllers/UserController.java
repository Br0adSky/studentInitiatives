package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.*;
import org.pikIt.studentInit.services.BidRepository;
import org.pikIt.studentInit.services.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("users/userPage")
public class UserController {
    private final Integer VOTES_FOR = 200;
    private final Integer VOTES_AGAINST = 200;
    private BidRepository bidRepository;
    private VotingRepository votingRepository;

    static void votingFor(User user, boolean yes, Bid bid, VotingRepository votingRepository, Integer VOTES_FOR) {
        Vote vote;
        if (yes) {
            if (votingRepository.findVoteByUserAndBid(user, bid) == null) {
                vote = new Vote(bid, user);

            } else {
                vote = votingRepository.findVoteByUserAndBid(user, bid);
                vote.setVotesAgainst(null);
            }
            vote.setVotesFor(1);
            votingRepository.save(vote);
            if (votingRepository.sumVotesFor() != null && votingRepository.sumVotesFor() >= VOTES_FOR) {
                bid.setStatus(BidStatus.Голосование_эксперт_состав);
            }
        }
    }

    static void votingAgainst(User user, boolean no, Bid bid, VotingRepository votingRepository, Integer VOTES_AGAINST) {
        Vote vote;
        if (no) {
            if (votingRepository.findVoteByUserAndBid(user, bid) == null) {
                vote = new Vote(bid, user);

            } else {
                vote = votingRepository.findVoteByUserAndBid(user, bid);
                vote.setVotesFor(null);
            }
            vote.setVotesAgainst(1);
            votingRepository.save(vote);
            if (votingRepository.sumVotesAgainst() != null && votingRepository.sumVotesAgainst() >= VOTES_AGAINST) {
                bid.setStatus(BidStatus.Модерация);
            }
        }
    }

    static void allBidsByName(Model model, BidRepository bidRepository, User user){
        model.addAttribute("message", "Все Ваши заявки");
        model.addAttribute("bids", bidRepository.findBidByAuthor(user));
    }


    @Autowired
    public void setVotingRepository(VotingRepository votingRepository) {
        this.votingRepository = votingRepository;
    }

    @Autowired
    public void setBidRepository(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    @PostMapping("/studVoteFor")
    public String studentVotingFor(@AuthenticationPrincipal User user, @RequestParam boolean yes, @RequestParam Bid bid) {
        votingFor(user, yes, bid, votingRepository, VOTES_FOR);
        return "redirect:/users/userPage";
    }

    @PostMapping("/studVoteAgainst")
    public String studentVotingAgainst(@AuthenticationPrincipal User user, @RequestParam boolean no, @RequestParam Bid bid) {
        votingAgainst(user, no, bid, votingRepository, VOTES_AGAINST);
        return "redirect:/users/userPage";
    }

    @GetMapping()
    public String main(Model model, @AuthenticationPrincipal User user) {
        BidController.replaceBidList(model, bidRepository);
        model.addAttribute("user", user);
        if(user.getRoles().contains(Role.SUPER_USER)){
            model.addAttribute("page", "/users/superUserPage");
            model.addAttribute("text","Перейти в личный кабинет");
        }
        else if(user.getRoles().contains(Role.MODERATOR)){
            model.addAttribute("text","Перейти в личный кабинет");
            model.addAttribute("page", "/bids");
        }
        else if(user.getRoles().contains(Role.EXPERT)){
            model.addAttribute("page", "/users/expertPage");
            model.addAttribute("text","Перейти в личный кабинет");
        }
        else{
            model.addAttribute("page","");
            model.addAttribute("text","");
        }
        model.addAttribute("studGroup", BidStatus.Голосование_студ_состав);
        return "users/userPage";
    }

    @GetMapping("/addNewBid")
    public String addBidButton() {
        return "redirect:/bids/addNewBid";
    }

    @PostMapping("/addBid")
    public String addBid(@AuthenticationPrincipal User user,
                         @RequestParam String text,
                         @RequestParam Integer priseFrom,
                         @RequestParam Integer priseTo,
                         @RequestParam String address,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            return "bids/addNewBid";
        }
        Bid bid = new Bid(text, user);
        if (address == null) {
            bid.setAddress("Не указано");
        }
        bid.setStatus(BidStatus.Новая);
        bid.setText(text);
        bid.setAddress(address);
        bid.setPriseFrom(priseFrom);
        bid.setPriseTo(priseTo);
        bidRepository.save(bid);
        model.addAttribute("bids", bidRepository.findAll());

        return "users/userPage";
    }

    @PostMapping("/filterText")
    public String filterText(@RequestParam String filterText,
                             Model model) {
        BidController.searchByText(filterText, model, bidRepository);
        return "users/userPage";
    }

    @PostMapping("/filterName")
    public String filterName(
            @RequestParam String filterName,
            @RequestParam String filterSurname,
            Model model) {
        BidController.searchByName(filterName, filterSurname, model, bidRepository);
        return "/users/userPage";
    }

    @PostMapping("/allBidsByName")
    public String allBidsByName(
            @AuthenticationPrincipal User user,
            Model model) {
        allBidsByName(model,bidRepository,user);
        return "users/userPage";
    }
    @PostMapping("/expert")
    public String expert(Model model) {
        model.addAttribute("bids", bidRepository.findByStatus(BidStatus.Голосование_эксперт_состав));
        return "users/userPage";
    }
    @GetMapping("/{bid}")
    public String bidEditForm(@AuthenticationPrincipal User user,
                              @PathVariable Bid bid, Model model) {
        if (user.getId().equals(bid.getAuthor().getId())) {
            model.addAttribute("userBid", bid);
            return "users/bidEditUser";
        } else {
            return "redirect:/users/userPage";
        }
    }

    @PostMapping("/save")
    public String bidSave(
            @Valid @RequestParam String text,
            @RequestParam Bid bid) {
        BidController.saveBid(text, bid, bidRepository);
        return "redirect:/users/userPage";
    }


}
