package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.services.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
//    private UserRepository userRepository;

//    @Autowired
//    public void setUserRepository(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    @Autowired
    public void setBidRepository(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    @GetMapping("/")
    public String homePage(){
        return "homePage";
    }

    @GetMapping("/bid")
    public String main(Model model) {
        model.addAttribute("bids", bidRepository.findAll());
        return "bid";
    }

    @PostMapping("/bid/addBid")
    public String addBid(@RequestParam Integer studentId, @RequestParam String text,
                         Model model) {

        Bid bid = new Bid(studentId, text);
        bidRepository.save(bid);
        model.addAttribute("bids", bidRepository.findAll());

        return "bid";
    }

    @PostMapping("/bid/filterText")
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

    @PostMapping("/bid/filterName")
    public String filterName(@RequestParam String filterName,
                             Model model) {
        if (filterName != null && !filterName.isEmpty()) {
            model.addAttribute("bids", bidRepository.findByName(filterName));
        } else {
            model.addAttribute("bids", bidRepository.findAll());
        }
        return "bid";
    }
}
