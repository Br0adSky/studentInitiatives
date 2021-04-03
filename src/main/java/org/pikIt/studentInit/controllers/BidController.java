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
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/bids/bidList")
@PreAuthorize("hasAuthority('MODERATOR')")
public class BidController {
    private BidRepository bidRepository;

    static void searchByName(String filterName, String filterSurname, Model model, BidRepository bidRepository, List<BidStatus> statuses) {
        if (filterName != null && !filterName.isBlank() || filterSurname != null && !filterSurname.isBlank()) {
            model.addAttribute("bids", bidRepository.findByNameAndSurnameContains(filterName, filterSurname));
            model.addAttribute("message", "Заявки найденного пользователя");
        } else {
            model.addAttribute("message", "Пользователь не найден");
            for(BidStatus status: statuses){
                UserController.replaceBidsByStatus(model, bidRepository, status);
            }

        }
    }

    static void searchByText(String filterText, Model model, BidRepository bidRepository, List<BidStatus> statuses) {
        model.addAttribute("message", "Заявки по введенному тексту");
        if (filterText != null && !filterText.isBlank()) {
            model.addAttribute("bids", bidRepository.findBidByTextContaining(filterText));
        } else {
            model.addAttribute("message", "Заявка не найдена");
            for(BidStatus status: statuses){
                UserController.replaceBidsByStatus(model, bidRepository, status);
            }
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

    @GetMapping
    public String bidList(Model model, @AuthenticationPrincipal User user) {
        UserController.replaceBidsByStatus(model, bidRepository, BidStatus.New);
        model.addAttribute("userRoles", user.getRoles());
        model.addAttribute("superUser", Role.SUPER_USER);
        return "bids/bidList";
    }

    @PostMapping("/toUserPage")
    public String toUserPage() {
        return "redirect:/users/userPage";
    }

    @PostMapping("/delete")
    public String deleteBid(@RequestParam Bid bid) {
        bidRepository.delete(bid);
        return "redirect:/bids/bidList";
    }

    @GetMapping("{bid}")
    public String bidEditForm(@PathVariable Bid bid, Model model, @AuthenticationPrincipal User user) {
        editForm(model, bid, user);
        bid.setStatus(BidStatus.Moderation);
        bidRepository.save(bid);
        return "bids/bidEdit";
    }

    @PostMapping()
    public String bidSave(
            @Valid @RequestParam String text,
            @RequestParam Bid bid, @RequestParam String address, @RequestParam Integer priseFrom, @RequestParam Integer priseTo) {
        saveBid(text, bid, bidRepository, address, priseFrom, priseTo, BidStatus.Voting_stud);
        return "redirect:/bids/bidList";
    }

    @PostMapping("/searchBidByAuthor")
    public String searchBid(
            @RequestParam String filterName,
            @RequestParam String filterSurname,
            Model model) {
        searchByName(filterName, filterSurname, model, bidRepository, Collections.singletonList(BidStatus.New));
        return "bids/bidList";
    }

    @PostMapping("/searchBidByText")
    public String searchBidByText(
            @RequestParam String filterText,
            Model model) {
        searchByText(filterText, model, bidRepository, Collections.singletonList(BidStatus.New));
        return "bids/bidList";
    }

    @Autowired
    public void setBidRepository(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }


}
