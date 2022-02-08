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

    //We need an inner class, helps us check whether or not the CollegiateSubreddit exists
    public class CollegiateSubredditOrError{
        Long id;
        CollegiateSubreddit collegeSubreddit;
        ResponseEntity<String> error;

        public CollegiateSubredditOrError(Long id){
            this.id = id;
        }

    }

    //Checks if the collegiateSubreddit is valid.
    //If the CollegiateSubreddit with toe.id exists, then toe.collegeSubreddit now refers to it.
    public CollegiateSubredditOrError doesCollegiateSubredditExist(
            CollegiateSubredditOrError toe){
        Optional<CollegiateSubreddit> optionalCollegiateSubreddit = collegiateSubredditRepository.findById(toe.id);

        if(optionalCollegiateSubreddit.isEmpty()){
            toe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("CollegiateSubreddit with id %d not found", toe.id));
        } else {
            toe.collegeSubreddit = optionalCollegiateSubreddit.get();
        }
        return toe;
    }

    //We ended up not needing this. Kinda doesn't make any sense. - Evan
    /** 
    public CollegiateSubredditOrError doesCollegiateSubredditBelongToCurrentUser(
            CollegiateSubredditOrError toe){
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        Long currentUserId = currentUser.getUser().getId();
        
        //collegeSubreddit doesn't necessarily have a getUser();
        //This comparison doesn't really make any sense, but for the sake of the project
        //We'll just go with it until further issue.
        Long collegiateSubredditId = toe.collegeSubreddit.getId();
        log.info("currentUserId={} CollegiateSubredditUserId={}", currentUserId, collegiateSubredditId);

        if(collegiateSubredditId != currentUserId){
            toe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("CollegiateSubreddit with id %d not found", toe.id));
        }
        return toe;
    }
    **/

    //ADD GET
    @ApiOperation(value = "Get a single CollegiateSubreddit")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getCollegiateSubredditById(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();
        CollegiateSubredditOrError toe = new CollegiateSubredditOrError(id);

        toe = doesCollegiateSubredditExist(toe);
        if (toe.error != null) {
            return toe.error;
        }
        /** //This doesn't make sense, collegiateSubreddits don't belong to anyone.
        toe = doesCollegiateSubredditBelongToCurrentUser(toe);
        if (toe.error != null) {
            return toe.error;
        }
        **/
        String body = mapper.writeValueAsString(toe.collegeSubreddit);
        return ResponseEntity.ok().body(body);
    }
}