package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.services.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/bids")
@PreAuthorize("hasAuthority('MODERATOR')")
public class BidController {
    private BidRepository bidRepository;

    static void searchByName(@RequestParam String filterName, @RequestParam String filterSurname, Model model, BidRepository bidRepository) {
        if (filterName != null && !filterName.isBlank() || filterSurname != null && !filterSurname.isBlank()) {
            model.addAttribute("bids", bidRepository.findByNameAndSurnameContains(filterName, filterSurname));
            model.addAttribute("message", "Заявки найденного пользователя");
        } else {
            model.addAttribute("message", "Пользователь не найден");
            model.addAttribute("bids", bidRepository.findAll());
        }
    }

    static void searchByText(@RequestParam String filterText, Model model, BidRepository bidRepository) {
        model.addAttribute("message", "Заявки по введенному тексту");
        if (filterText != null && !filterText.isBlank()) {
            model.addAttribute("bids", bidRepository.findBidByTextContaining(filterText));
        } else {
            model.addAttribute("bids", bidRepository.findAll());
        }
    }

    static void saveBid(@RequestParam String text,
                        @RequestParam Bid bid, BidRepository bidRepository) {
        bid.setText(text);
        bidRepository.save(bid);
    }
    static void replaceBidList(Model model, BidRepository bidRepository){
        model.addAttribute("message", "Все текущие заявки и их статус");
        model.addAttribute("bids", bidRepository.findAll());

    }
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/addNewBid")
    public String addNew(){
        return "bids/addNewBid";
    }
    @GetMapping
    public String bidList(Model model) {
        replaceBidList(model,bidRepository);
        return "bids/bidList";
    }

    @GetMapping("{bid}")
    public String bidEditForm(@PathVariable Bid bid, Model model) {
        model.addAttribute("bid2", bid);
        return "bids/bidEdit";
    }

    @PostMapping()
    public String bidSave(
            @Valid @RequestParam String text,
            @RequestParam Bid bid) {
        saveBid(text, bid, bidRepository);
        return "redirect:/bids";
    }

    @PostMapping("/searchBidByAuthor")
    public String searchBid(
            @RequestParam String filterName,
            @RequestParam String filterSurname,
            Model model) {
        searchByName(filterName, filterSurname, model, bidRepository);
        return "bids/bidList";
    }

    @PostMapping("/searchBidByText")
    public String searchBidByText(
            @RequestParam String filterText,
            Model model) {
        searchByText(filterText, model, bidRepository);
        return "bids/bidList";
    }

    @Autowired
    public void setBidRepository(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }


}
