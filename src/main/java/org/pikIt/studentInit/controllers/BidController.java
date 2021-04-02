package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.BidStatus;
import org.pikIt.studentInit.model.Role;
import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.services.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    static void saveBid(String text, Bid bid, BidRepository bidRepository,
                        String address, Integer priseFrom, Integer priseTo, BidStatus status) {
        bid.setText(text);
        bid.setAddress(address);
        bid.setPriseFrom(priseFrom);
        bid.setPriseTo(priseTo);
        bid.setStatus(status);
        bidRepository.save(bid);
    }

    static void editForm(Model model, Bid bid, User user) {
        model.addAttribute("bid2", bid);
        model.addAttribute("userRoles", user.getRoles());
        model.addAttribute("moderator", Role.MODERATOR);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/addNewBid")
    public String addNew() {
        return "bids/addNewBid";
    }

    @GetMapping
    public String bidList(Model model) {
        UserController.replaceBidsByStatus(model,bidRepository,BidStatus.Новая);
        model.addAttribute("statuses", BidStatus.values());
        return "bids/bidList";
    }

    @PostMapping("/delete")
    public String deleteBid(@RequestParam Bid bid){
        bidRepository.delete(bid);
        return "redirect:/bids";
    }

    @GetMapping("{bid}")
    public String bidEditForm(@PathVariable Bid bid, Model model, @AuthenticationPrincipal User user) {
        editForm(model, bid, user);
        bid.setStatus(BidStatus.Модерация);
        bidRepository.save(bid);
        return "bids/bidEdit";
    }

    @PostMapping()
    public String bidSave(
            @Valid @RequestParam String text,
            @RequestParam Bid bid, @RequestParam String address, @RequestParam Integer priseFrom, @RequestParam Integer priseTo) {
        saveBid(text, bid, bidRepository, address, priseFrom, priseTo, BidStatus.Голосование_студ_состав);
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
