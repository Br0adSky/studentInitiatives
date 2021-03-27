package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.services.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class MainPage {

    private BidRepository bidRepository;

    @Autowired
    public void setBidRepository(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    @GetMapping("/")
    public String homePage() {
        return "homePage";
    }

    @GetMapping("/mainPage")
    public String main(Model model) {
        model.addAttribute("bids", bidRepository.findAll());
        return "mainPage";
    }

    @PostMapping("addBid")
    public String addBid(@AuthenticationPrincipal User user,
                         @RequestParam String text,
                         Model model) {

        Bid bid = new Bid(text, user);
        bidRepository.save(bid);
        model.addAttribute("bids", bidRepository.findAll());

        return "mainPage";
    }

    @PostMapping("filterText")
    public String filterText(@RequestParam String filterText,
                             Model model) {
        if (filterText != null && !filterText.isEmpty()) {
            model.addAttribute("bids", bidRepository.findBidByTextContaining(filterText));
        } else {
            model.addAttribute("bids", bidRepository.findAll());
        }
        return "mainPage";
    }

    @PostMapping("filterName")
    public String filterName(
            @RequestParam String filterName,
            Model model) {
        if (filterName != null && !filterName.isEmpty()) {
            model.addAttribute("bids", bidRepository.findBidByAuthor_NameContaining(filterName));
        } else {
            model.addAttribute("bids", bidRepository.findAll());
        }
        return "mainPage";
    }

    @PostMapping("allBidsByName")
    public String allBidsByName(
            @AuthenticationPrincipal User user,
            Model model) {
        model.addAttribute("bids", bidRepository.findBidByAuthor(user));
        return "mainPage";
    }
}
