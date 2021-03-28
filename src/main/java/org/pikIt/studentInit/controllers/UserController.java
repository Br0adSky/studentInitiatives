package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.services.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("users/userPage")
public class UserController {

    private BidRepository bidRepository;

    @Autowired
    public void setBidRepository(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }


    @GetMapping()
    public String main(Model model) {
        model.addAttribute("message", "Все текущие заявки и их статус");
        model.addAttribute("bids", bidRepository.findAll());
        return "users/userPage";
    }

    @PostMapping("/addBid")
    public String addBid(@AuthenticationPrincipal User user,
                         @RequestParam String text,
                         Model model) {

        Bid bid = new Bid(text, user, false);

        bidRepository.save(bid);
        model.addAttribute("bids", bidRepository.findAll());

        return "users/userPage";
    }

    @PostMapping("/filterText")
    public String filterText(@RequestParam String filterText,
                             Model model) {
        model.addAttribute("message", "Заявки по введенному тексту");
        if (filterText != null && !filterText.isEmpty()) {
            model.addAttribute("bids", bidRepository.findBidByTextContaining(filterText));
        } else {
            model.addAttribute("bids", bidRepository.findAll());
        }
        return "users/userPage";
    }

    @PostMapping("/filterName")
    public String filterName(
            @RequestParam String filterName,
            @RequestParam String filterSurname,
            Model model) {
        if (filterName != null && !filterName.isEmpty() || filterSurname != null && !filterSurname.isEmpty()) {
            model.addAttribute("bids", bidRepository.findByNameAndSurnameContains(filterName, filterSurname));
            model.addAttribute("message", "Заявки найденного пользователя");
        } else {
            model.addAttribute("message", "Пользователь не найден");
            model.addAttribute("bids", bidRepository.findAll());
        }
        return "users/userPage";
    }

    @PostMapping("/allBidsByName")
    public String allBidsByName(
            @AuthenticationPrincipal User user,
            Model model) {
        model.addAttribute("message", "Все Ваши заявки");
        model.addAttribute("bids", bidRepository.findBidByAuthor(user));
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
            @RequestParam String text,
            @RequestParam Bid bid) {
        bid.setText(text);
        bidRepository.save(bid);
        return "redirect:/users/userPage";
    }
}
