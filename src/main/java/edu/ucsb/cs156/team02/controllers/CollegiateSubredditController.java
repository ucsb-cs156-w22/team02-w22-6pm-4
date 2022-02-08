package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.CollegiateSubreddit;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.CollegiateSubredditRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Api(description = "CollegiateSubreddit")
@RequestMapping("/api/collegiateSubreddits")
@RestController
@Slf4j
public class CollegiateSubredditController extends ApiController {


    public class CollegiateSubredditOrError {
        Long id;
        CollegiateSubreddit collegiateSubreddit;
        ResponseEntity<String> error;

        public CollegiateSubredditOrError(Long id) {
            this.id = id;
        }
    }
    @Autowired
    CollegiateSubredditRepository collegiateSubredditRepository;

    @Autowired
    ObjectMapper mapper;



    @ApiOperation(value = "List all collegiate subreddits in database")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<CollegiateSubreddit> getCollegiateSubreddits() {
        loggingService.logMethod();
        Iterable<CollegiateSubreddit> collegiateSubs = collegiateSubredditRepository.findAll();
        return collegiateSubs;
    }

    @ApiOperation(value = "Create a new CollegiateSubreddit")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public CollegiateSubreddit postCollegiateSubreddit(
            @ApiParam("name") @RequestParam String name,
            @ApiParam("location") @RequestParam String location,
            @ApiParam("subreddit") @RequestParam String subreddit) {
        loggingService.logMethod();

        CollegiateSubreddit collegiateSubreddit = new CollegiateSubreddit();
        collegiateSubreddit.setName(name);
        collegiateSubreddit.setLocation(location);
        collegiateSubreddit.setSubreddit(subreddit);
        CollegiateSubreddit savedcollegiateSubs = collegiateSubredditRepository.save(collegiateSubreddit);
        return savedcollegiateSubs;
    }

    @ApiOperation(value = "Delete a CollegiateSubreddit")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteCollegiateSubreddit(
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        CollegiateSubredditOrError toe = new CollegiateSubredditOrError(id);

        toe = doesCollegiateSubredditExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

   
        collegiateSubredditRepository.deleteById(id);
        return ResponseEntity.ok().body(String.format("record with id %d deleted", id));

    }

    // @ApiOperation(value = "Delete another user's CollegiateSubreddit")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @DeleteMapping("/admin")
    // public ResponseEntity<String> deleteCollegiateSubreddit_Admin(
    //         @ApiParam("id") @RequestParam Long id) {
    //     loggingService.logMethod();

    //     CollegiateSubredditOrError toe = new CollegiateSubredditOrError(id);

    //     toe = doesCollegiateSubredditExist(toe);
    //     if (toe.error != null) {
    //         return toe.error;
    //     }

    //     collegiateSubredditRepository.deleteById(id);

    //     return ResponseEntity.ok().body(String.format("colegiate subreddit with id %d deleted", id));

    // }

     /**
     * Pre-conditions: toe.id is value to look up, toe.todo and toe.error are null
     * 
     * Post-condition: if todo with id toe.id exists, toe.todo now refers to it, and
     * error is null.
     * Otherwise, todo with id toe.id does not exist, and error is a suitable return
     * value to
     * report this error condition.
     */
    public CollegiateSubredditOrError doesCollegiateSubredditExist(CollegiateSubredditOrError toe) {

        Optional<CollegiateSubreddit> optionalCollegiateSubreddit = collegiateSubredditRepository.findById(toe.id);

        if (optionalCollegiateSubreddit.isEmpty()) {
            toe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("record with id %d not found", toe.id));
        } else {
            toe.collegiateSubreddit = optionalCollegiateSubreddit.get();
        }
        return toe;
    }

    /**
     * Pre-conditions: toe.todo is non-null and refers to the todo with id toe.id,
     * and toe.error is null
     * 
     * Post-condition: if todo belongs to current user, then error is still null.
     * Otherwise error is a suitable
     * return value.
     */


}