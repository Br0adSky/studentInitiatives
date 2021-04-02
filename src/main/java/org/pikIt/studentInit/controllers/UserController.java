package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.*;
import org.pikIt.studentInit.services.BidRepository;
import org.pikIt.studentInit.services.MediaTypeService;
import org.pikIt.studentInit.services.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;


@Controller
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("users/userPage")
public class UserController {
    private final Integer VOTES_FOR = 200;
    private final Integer VOTES_AGAINST = 200;
    private final ServletContext context;
    private final BidRepository bidRepository;
    private final VotingRepository votingRepository;

    @Autowired
    public UserController(ServletContext context, VotingRepository votingRepository, BidRepository bidRepository) {
        this.context = context;
        this.votingRepository = votingRepository;
        this.bidRepository = bidRepository;

    }
    static void main(Model model, BidRepository bidRepository, User user){
        model.addAttribute("message", "Все текущие заявки и их статус");
        model.addAttribute("bids", bidRepository.findAll());
        model.addAttribute("user", user);
        model.addAttribute("text", "Перейти в личный кабинет");
        if (user.getRoles().contains(Role.SUPER_USER)) {
            model.addAttribute("page", "/users/superUserPage");
        } else if (user.getRoles().contains(Role.MODERATOR)) {
            model.addAttribute("page", "/bids");
        } else if (user.getRoles().contains(Role.EXPERT)) {
            model.addAttribute("page", "/users/expertPage");
        } else {
            model.addAttribute("page", "");
            model.addAttribute("text", "");
        }
        model.addAttribute("studGroup", BidStatus.Голосование_студ_состав);
    }

    static void votingFor(User user, boolean yes, Bid bid, VotingRepository votingRepository, Integer VOTES_FOR, BidStatus status) {
        Vote vote;
        if (yes) {
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
    }

    static void votingAgainst(User user, boolean no, Bid bid, VotingRepository votingRepository, Integer VOTES_AGAINST, BidRepository bidRepository) {
        Vote vote;
        if (no) {
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
    }

    static void allBidsByName(Model model, BidRepository bidRepository, User user) {
        model.addAttribute("message", "Все Ваши заявки");
        model.addAttribute("bids", bidRepository.findBidByAuthor(user));
    }

    static void replaceBidsByStatus(Model model, BidRepository bidRepository, BidStatus bidStatus) {
        model.addAttribute("bids", bidRepository.findByStatus(bidStatus));
    }

    @PostMapping("/studVoteFor")
    public String studentVotingFor(@AuthenticationPrincipal User user, @RequestParam boolean yes, @RequestParam Bid bid) {
        votingFor(user, yes, bid, votingRepository, VOTES_FOR, BidStatus.Голосование_эксперт_состав);
        return "redirect:/users/userPage";
    }

    @PostMapping("/studVoteAgainst")
    public String studentVotingAgainst(@AuthenticationPrincipal User user, @RequestParam boolean no, @RequestParam Bid bid) {
        votingAgainst(user, no, bid, votingRepository, VOTES_AGAINST, bidRepository);
        return "redirect:/users/userPage";
    }

    @GetMapping()
    public String main(Model model, @AuthenticationPrincipal User user) {
        main(model,bidRepository,user);
        return "users/userPage";
    }

    @PostMapping("/delete")
    public String deleteBid(@RequestParam Bid bid){
        bidRepository.delete(bid);
        return "redirect:/users/userPage";
    }

    @GetMapping("/addNewBid")
    public String addBidButton() {
        return "redirect:/bids/addNewBid";
    }

    @PostMapping("/addBid")
    public String addBid(@AuthenticationPrincipal User user,
                         @RequestParam String text,
                         @RequestParam Integer priseFrom,
                         @RequestParam Integer priseTo,
                         @RequestParam String address,
                         @RequestParam("file") MultipartFile file,
                         Model model) throws IOException {
        Bid bid = new Bid(text, user);
        if(file.getOriginalFilename()!=null){
            String UPLOAD_PATH = context.getRealPath("") + File.separator + "uploadedFiles" + File.separator;
            FileCopyUtils.copy(file.getBytes(), new File(UPLOAD_PATH + file.getOriginalFilename()));
            String fileName = file.getOriginalFilename();
            bid.setFileName(fileName);
        }
        else
            bid.setFileName("Без файла");
        if (address == null) {
            bid.setAddress("Не указано");
        }
        bid.setStatus(BidStatus.Новая);
        bid.setText(text);
        bid.setAddress(address);
        bid.setPriseFrom(priseFrom);
        bid.setPriseTo(priseTo);
        bidRepository.save(bid);
        model.addAttribute("bids", bidRepository.findAll());

        return "users/userPage";
    }

    @GetMapping("/getFile/{fileName}")
    public void getFile(@PathVariable String fileName, HttpServletResponse response) throws IOException {

        MediaType mediaType = MediaTypeService.getMediaTypeForFileName(this.context, fileName);
        String UPLOAD_PATH = context.getRealPath("") + File.separator + "uploadedFiles" + File.separator;
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
                             Model model) {
        BidController.searchByText(filterText, model, bidRepository);
        return "users/userPage";
    }

    @PostMapping("/filterName")
    public String filterName(
            @RequestParam String filterName,
            @RequestParam String filterSurname,
            Model model) {
        BidController.searchByName(filterName, filterSurname, model, bidRepository);
        return "/users/userPage";
    }

    @PostMapping("/allBidsByName")
    public String allBidsByName(
            @AuthenticationPrincipal User user,
            Model model) {
        allBidsByName(model, bidRepository, user);
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
        BidController.saveBid(text, bid, bidRepository, address, priseFrom, priseTo, BidStatus.Новая);
        return "redirect:/users/userPage";
    }

    @PostMapping("/studVotes")
    public String replaceAvailableVotes(Model model) {
        replaceBidsByStatus(model, bidRepository, BidStatus.Голосование_студ_состав);
        model.addAttribute("message", "Доступные голосования");
        return "users/userPage";
    }


}
