package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.BidStatus;
import org.pikIt.studentInit.model.Role;
import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.services.BidRepository;
import org.pikIt.studentInit.services.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/bids/bidList")
@PreAuthorize("hasAuthority('MODERATOR')")
public class BidController {
    private final String UPLOAD_PATH;
    private BidRepository bidRepository;

    @Autowired
    public BidController(ServletContext context) {
        UPLOAD_PATH = context.getRealPath("") + "uploadedFiles" + File.separator;
    }

    static void searchByName(String filterName, String filterSurname, Model model, BidRepository bidRepository, List<BidStatus> statuses) {
        if (filterName != null && !filterName.isBlank() || filterSurname != null && !filterSurname.isBlank()) {
            model.addAttribute("bids", bidRepository.findByNameAndSurnameContains(filterName, filterSurname));
            model.addAttribute("message", "Заявки найденного пользователя");
        } else {
            model.addAttribute("message", "Пользователь не найден");
            for (BidStatus status : statuses) {
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
            for (BidStatus status : statuses) {
                UserController.replaceBidsByStatus(model, bidRepository, status);
            }
        }
    }

    static String saveBid(Bid bid, BindingResult bindingResult, Model model, BidRepository bidRepository,
                          BidStatus status, String page, MultipartFile file, String UPLOAD_PATH) throws IOException {
        if (bindingResult.hasErrors()) {
            model.mergeAttributes(ControllerUtils.getErrorMap(bindingResult));
            model.addAttribute("bid", bid);
            return "bids/bidEdit";
        } else {
            if (!file.isEmpty()) {
                FileCopyUtils.copy(file.getBytes(), new File(UPLOAD_PATH + file.getOriginalFilename()));
                String fileName = file.getOriginalFilename();
                bid.setFileName(fileName);
            }
            bid.setStatus(status);
            bidRepository.save(bid);
            return page;
        }
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
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping()
    public String bidSave(
            @Valid Bid bid, BindingResult bindingResult, Model model, @RequestParam(required = false, value = "file") MultipartFile file, @AuthenticationPrincipal User user) throws IOException {
        if(user.getRoles().contains(Role.MODERATOR))
            return saveBid(bid, bindingResult, model, bidRepository, BidStatus.Voting_stud, "redirect:/bids/bidList", file, UPLOAD_PATH);
        return BidController.saveBid(bid, bindingResult, model,bidRepository,BidStatus.New, "redirect:/users/userPage", file, UPLOAD_PATH);
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
