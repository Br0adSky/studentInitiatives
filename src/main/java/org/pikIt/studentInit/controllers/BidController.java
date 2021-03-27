package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.services.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bids")
//@PreAuthorize("hasAuthority('MODERATOR')")
public class BidController {
    private BidRepository bidRepository;

    @GetMapping
    public String bidList(Model model) {
        model.addAttribute("bids", bidRepository.findAll());
        return "bidList";
    }

    @GetMapping("{bid}")
    public String bidEditForm(@PathVariable Bid bid, Model model) {
        model.addAttribute("bid2", bid);
        return "bidEdit";
    }

    @PostMapping()
    public String bidSave(
            @RequestParam String text,
            @RequestParam Bid bid) {
        bid.setText(text);
        bidRepository.save(bid);
        return "redirect:/bids";
    }

    public BidRepository getBidRepository() {
        return bidRepository;
    }

    @Autowired
    public void setBidRepository(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }


}
