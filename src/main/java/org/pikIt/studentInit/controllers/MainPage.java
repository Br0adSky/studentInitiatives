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

import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/bid")
    public String main(Model model) {
        model.addAttribute("bids", bidRepository.findAll());
        return "bid";
    }

    @PostMapping("addBid")
    public String addBid(@AuthenticationPrincipal User user,
                         @RequestParam String text,
                         Model model) {

        Bid bid = new Bid(text, user);
        bidRepository.save(bid);
        model.addAttribute("bids", bidRepository.findAll());

        return "bid";
    }

    @PostMapping("filterText")
    public String filterText(@RequestParam String filterText,
                             Model model) {
        List<Bid> texts = new ArrayList<>();
        if (filterText != null && !filterText.isEmpty()) {
            for (Bid bid : bidRepository.findAll()) {
                if (bid.getText().contains(filterText)) {
                    texts.add(bid);
                }
            }
            model.addAttribute("bids", texts);
        } else {
            model.addAttribute("bids", bidRepository.findAll());
        }
        return "bid";
    }

    @PostMapping("filterName")
    public String filterName(
            @RequestParam String filterName,
            Model model) {
        List<Bid> name = new ArrayList<>();
        if (filterName != null && !filterName.isEmpty()) {
            for (Bid bid : bidRepository.findAll()) {
                if (bid.getAuthorName().contains(filterName)) {
                    name.add(bid);
                }
            }
            model.addAttribute("bids", name);
        } else {
            model.addAttribute("bids", bidRepository.findAll());
        }
        return "bid";
    }

    @PostMapping("allBidsByName")
    public String allBidsByName(
            @AuthenticationPrincipal User user,
            Model model) {
        model.addAttribute("bids", bidRepository.findBidByAuthor(user));
        return "bid";
    }
}
