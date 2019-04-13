package nl.fontys.kwetter.controllers.api;

import nl.fontys.kwetter.exceptions.*;
import nl.fontys.kwetter.models.Kwetter;
import nl.fontys.kwetter.models.UUIDRequest;
import nl.fontys.kwetter.models.User;
import nl.fontys.kwetter.service.IKwetterService;
import nl.fontys.kwetter.service.ILoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/kwetter", produces = MediaType.APPLICATION_JSON_VALUE)
public class KwetterController {

    private final IKwetterService kwetterService;

    private final ILoginService loginService;

    @Autowired
    public KwetterController(IKwetterService kwetterService, ILoginService loginService) {
        this.kwetterService = kwetterService;
        this.loginService = loginService;
    }

    @PostMapping("/remove/{id}")
    @PreAuthorize("hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity removeKwetter(@PathVariable UUID id, @RequestBody UUIDRequest UUIDRequest) throws KwetterDoesNotExist, UserDoesNotExist, CouldNotDelete {
        kwetterService.removeKwetter(id, UUIDRequest.getId());
        return ResponseEntity.ok("Removed kwetter");
    }

    @PostMapping("/search_for")
    public ResponseEntity<List<Kwetter>> searchForKwetter(@RequestBody String searchTerm) {
        List<Kwetter> kwetter = kwetterService.searchForKwetter(searchTerm);
        return ResponseEntity.ok(kwetter);
    }

    @PostMapping("/create")
    public ResponseEntity<Kwetter> createKwetter(@RequestBody Kwetter kwetter) throws UserDoesNotExist, InvalidModelException, CannotLoginException {
        User user = loginService.autoLogin();
        Kwetter createdKwetter = kwetterService.createKwetter(user.getId(), kwetter);
        return ResponseEntity.ok(createdKwetter);
    }

    @PostMapping("/heart")
    public ResponseEntity heartKwetter(@RequestBody UUIDRequest UUIDRequest) throws KwetterDoesNotExist, UserDoesNotExist, InvalidModelException, CannotLoginException {
        User user = loginService.autoLogin();
        kwetterService.heartKwetter(user.getId(), UUIDRequest.getId());
        return ResponseEntity.ok("Hearted kwetter");
    }

    @PostMapping("/remove_heart")
    public ResponseEntity removeHeartKwetter(@RequestBody UUIDRequest UUIDRequest) throws KwetterDoesNotExist, UserDoesNotExist, InvalidModelException, CannotLoginException {
        User user = loginService.autoLogin();
        kwetterService.removeHeartKwetter(user.getId(), UUIDRequest.getId());
        return ResponseEntity.ok("Removed Heart from kwetter");
    }

    @PostMapping("/report")
    public ResponseEntity reportKwetter(@RequestBody UUIDRequest UUIDRequest) throws KwetterDoesNotExist, UserDoesNotExist, InvalidModelException, CannotLoginException {
        User user = loginService.autoLogin();
        kwetterService.reportKwetter(user.getId(), UUIDRequest.getId());
        return ResponseEntity.ok("Report kwetter");
    }

    @PostMapping("/remove_report")
    public ResponseEntity removeReportKwetter(@RequestBody UUIDRequest UUIDRequest) throws KwetterDoesNotExist, UserDoesNotExist, InvalidModelException, CannotLoginException {
        User user = loginService.autoLogin();
        kwetterService.removeReportKwetter(user.getId(), UUIDRequest.getId());
        return ResponseEntity.ok("Removed Report from kwetter");
    }

    @GetMapping("/mentioned")
    public ResponseEntity<List<Kwetter>> getMentionedKwetters() throws UserDoesNotExist, InvalidModelException, CannotLoginException {
        User user = loginService.autoLogin();
        List<Kwetter> mentionedKwetters = kwetterService.getMentionedKwetters(user.getId());
        return ResponseEntity.ok(mentionedKwetters);
    }

    @GetMapping("/most_recent")
    public ResponseEntity<List<Kwetter>> getMostRecentKwetters() throws UserDoesNotExist, InvalidModelException, CannotLoginException {
        User user = loginService.autoLogin();
        List<Kwetter> recentKwetters = kwetterService.getMostRecentKwetters(user.getId());
        return ResponseEntity.ok(recentKwetters);
    }

    @GetMapping("/hearted")
    public ResponseEntity<List<Kwetter>> getHeartedKwetters() throws UserDoesNotExist, InvalidModelException, CannotLoginException {
        User user = loginService.autoLogin();
        List<Kwetter> heartedKwetters = kwetterService.getHeartedKwetters(user.getId());
        return ResponseEntity.ok(heartedKwetters);
    }

    @GetMapping("/timeline")
    public ResponseEntity<List<Kwetter>> getTimeLine() throws UserDoesNotExist, InvalidModelException, CannotLoginException {
        User user = loginService.autoLogin();
        List<Kwetter> timelineKwetters = kwetterService.getTimeline(user.getId());
        return ResponseEntity.ok(timelineKwetters);
    }
}
