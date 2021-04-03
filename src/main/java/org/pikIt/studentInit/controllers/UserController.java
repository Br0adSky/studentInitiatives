package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.*;
import org.pikIt.studentInit.services.BidRepository;
import org.pikIt.studentInit.services.ControllerUtils;
import org.pikIt.studentInit.services.MediaTypeUtils;
import org.pikIt.studentInit.services.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.Arrays;


@Controller
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("users/userPage")
public class UserController {
    private final Integer VOTES_FOR = 200;
    private final Integer VOTES_AGAINST = 200;
    private final ServletContext context;
    private final BidRepository bidRepository;
    private final VotingRepository votingRepository;
    private final String UPLOAD_PATH;


    @Autowired
    public UserController(ServletContext context, VotingRepository votingRepository, BidRepository bidRepository) {
        this.context = context;
        this.votingRepository = votingRepository;
        this.bidRepository = bidRepository;
        UPLOAD_PATH = context.getRealPath("") + "uploadedFiles" + File.separator;
    }

    static void createDefaultParams(Model model, User user){
        model.addAttribute("message", "Все текущие заявки и их статус");
        model.addAttribute("user", user);
        model.addAttribute("text", "Перейти в личный кабинет");
        if (user.getRoles().contains(Role.SUPER_USER)) {
            model.addAttribute("page", "/users/superUserPage");
        } else if (user.getRoles().contains(Role.MODERATOR)) {
            model.addAttribute("page", "/bids/bidList");
        } else if (user.getRoles().contains(Role.EXPERT)) {
            model.addAttribute("page", "/users/expertPage");
        } else {
            model.addAttribute("page", "");
            model.addAttribute("text", "");
        }
        model.addAttribute("studGroup", BidStatus.Voting_stud);
    }

    static void votingFor(User user,  Bid bid, VotingRepository votingRepository, Integer VOTES_FOR, BidStatus status) {
        Vote vote;
        if (votingRepository.findVoteByUserAndBid(user, bid) == null) {
            vote = new Vote(bid, user);

        } else {
            vote = votingRepository.findVoteByUserAndBid(user, bid);
            vote.setVotesAgainst(null);
        }
        vote.setVotesFor(1);
        votingRepository.save(vote);
        if (votingRepository.sumVotesFor() != null && votingRepository.sumVotesFor() >= VOTES_FOR) {
            bid.setStatus(status);
        }
    }

    static void votingAgainst(User user, Bid bid, VotingRepository votingRepository, Integer VOTES_AGAINST, BidRepository bidRepository) {
        Vote vote;
        if (votingRepository.findVoteByUserAndBid(user, bid) == null) {
            vote = new Vote(bid, user);

        } else {
            vote = votingRepository.findVoteByUserAndBid(user, bid);
            vote.setVotesFor(null);
        }
        vote.setVotesAgainst(1);
        votingRepository.save(vote);
        if (votingRepository.sumVotesAgainst() != null && votingRepository.sumVotesAgainst() >= VOTES_AGAINST) {
            for(Vote v : votingRepository.findVoteByBid(bid)){
                votingRepository.delete(v);
            }
            bidRepository.delete(bid);
            }
    }

    static void allBidsByName(Model model, BidRepository bidRepository, User user) {
        model.addAttribute("message", "Все Ваши заявки");
        model.addAttribute("bids", bidRepository.findBidByAuthor(user));
    }

    static void replaceBidsByStatus(Model model, BidRepository bidRepository, BidStatus bidStatus) {
        model.addAttribute("bids", bidRepository.findByStatus(bidStatus));
    }

    @PostMapping("/studVoteFor")
    public String studentVotingFor(@AuthenticationPrincipal User user,  @RequestParam Bid bid) {
        votingFor(user, bid, votingRepository, VOTES_FOR, BidStatus.Voting_expert);
        return "redirect:/users/userPage";
    }

    @PostMapping("/studVoteAgainst")
    public String studentVotingAgainst(@AuthenticationPrincipal User user, @RequestParam Bid bid) {
        votingAgainst(user, bid, votingRepository, VOTES_AGAINST, bidRepository);
        return "redirect:/users/userPage";
    }

    @GetMapping("/addNewBid")
    public String addNew(Model model, @AuthenticationPrincipal User user) {
        Bid bid = new Bid();
        bid.setAuthor(user);
        model.addAttribute("bid", bid);
        return "bids/addNewBid";
    }

    @GetMapping()
    public String main(Model model, @AuthenticationPrincipal User user) {
        createDefaultParams(model, user);
        model.addAttribute("bids", bidRepository.findAll());
        return "users/userPage";
    }

    @PostMapping("/delete")
    public String deleteBid(@RequestParam Bid bid){
        bidRepository.delete(bid);
        return "redirect:/users/userPage";
    }

    @PostMapping("/addNewBid")
    public String addBid(@AuthenticationPrincipal User user,
                         @Valid Bid bid,
                         BindingResult bindingResult,
                         Model model,
                         @RequestParam("file") MultipartFile file) throws IOException {
        bid.setAuthor(user);
        if(bindingResult.hasErrors()){
            model.mergeAttributes(ControllerUtils.getErrorMap(bindingResult));
            model.addAttribute("bid", bid);
            return "bids/addNewBid";
        } else{
            if(!file.isEmpty()){
                FileCopyUtils.copy(file.getBytes(), new File(UPLOAD_PATH + file.getOriginalFilename()));
                String fileName = file.getOriginalFilename();
                bid.setFileName(fileName);
            }
            bid.setStatus(BidStatus.New);
            bidRepository.save(bid);
        }
        model.addAttribute("bids", bidRepository.findAll());
        createDefaultParams(model,user);
        return "users/userPage";
    }

    @GetMapping("/getFile/{fileName}")
    public void getFile(@PathVariable String fileName, HttpServletResponse response) throws IOException {

        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.context, fileName);
        File file = new File(UPLOAD_PATH + fileName);
        response.setContentType(mediaType.getType());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());
        response.setContentLength((int) file.length());
        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        outStream.flush();
        inStream.close();
    }

    @PostMapping("/filterText")
    public String filterText(@RequestParam String filterText,
                             Model model, @AuthenticationPrincipal User user) {
        BidController.searchByText(filterText, model, bidRepository, Arrays.asList(BidStatus.values().clone()));
        createDefaultParams(model,user);
        return "users/userPage";
    }

    @PostMapping("/filterName")
    public String filterName(
            @RequestParam String filterName,
            @RequestParam String filterSurname,
            Model model, @AuthenticationPrincipal User user) {
        BidController.searchByName(filterName, filterSurname, model, bidRepository, Arrays.asList(BidStatus.values().clone()));
        createDefaultParams(model,user);
        return "/users/userPage";
    }

    @PostMapping("/allBidsByName")
    public String allBidsByName(
            @AuthenticationPrincipal User user,
            Model model) {
        allBidsByName(model, bidRepository, user);
        createDefaultParams(model,user);
        return "users/userPage";
    }

    @GetMapping("/{bid}")
    public String bidEditForm(@PathVariable Bid bid, Model model, @AuthenticationPrincipal User user) {
        BidController.editForm(model, bid, user);
        return "bids/bidEdit";

    }

    @PostMapping("/save")
    public String bidSave(
            @Valid @RequestParam String text,
            @RequestParam Bid bid, @RequestParam String address, @RequestParam Integer priseFrom, @RequestParam Integer priseTo) {
        BidController.saveBid(text, bid, bidRepository, address, priseFrom, priseTo, BidStatus.New);
        return "redirect:/users/userPage";
    }

    @PostMapping("/studVotes")
    public String replaceAvailableVotes(Model model, @AuthenticationPrincipal User user) {
        replaceBidsByStatus(model, bidRepository, BidStatus.Voting_stud);
        model.addAttribute("message", "Доступные голосования");
        createDefaultParams(model,user);
        return "users/userPage";
    }


}
