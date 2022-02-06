package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;
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

@Api(description = "UCSBSubject")
@RequestMapping("api/UCSBSubjects")
@RestController
@Slf4j
public class UCSBSubjectController extends ApiController {

    /**
     * This inner class helps us factor out some code for checking
     * whether todos exist, and whether they belong to the current user,
     * along with the error messages pertaining to those situations. It
     * bundles together the state needed for those checks.
     */
     public class UCSBSubjectOrError {
         Long id;
         UCSBSubject UCSBSubject;
         ResponseEntity<String> error;

         public UCSBSubjectOrError(Long id) {
             this.id = id;
         }
     }

    @Autowired
    UCSBSubjectRepository UCSBSubjectRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all UCSBSubjects")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBSubject> allUCSBSubject() {
        loggingService.logMethod();
        Iterable<UCSBSubject> UCSBSubject = UCSBSubjectRepository.findAll();
        return UCSBSubject;
    }

    // @ApiOperation(value = "List this user's UCSBSubject")
    // @PreAuthorize("hasRole('ROLE_USER')")
    // @GetMapping("/all")
    // public Iterable<UCSBSubject> thisUsersUCSBSubject() {
    //     loggingService.logMethod();
    //     CurrentUser currentUser = getCurrentUser();
    //     Iterable<UCSBSubject> UCSBSubject = UCSBSubjectRepository.findBySubject(currentUser.getUser().getId());
    //     return UCSBSubject;
    // }

    // @ApiOperation(value = "Get a single UCSBSubject (if it belongs to current user)")
    // @PreAuthorize("hasRole('ROLE_USER')")
    // @GetMapping("")
    // public ResponseEntity<String> getUCSBSubjectById(
    //         @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
    //     loggingService.logMethod();
    //     // UCSBSubjectOrError toe = new UCSBSubjectOrError(id);

    //     // toe = doesUCSBSubjectExist(toe);
    //     // if (toe.error != null) {
    //     //     return toe.error;
    //     // }
    //     // toe = doesUCSBSubjectBelongToCurrentUser(toe);
    //     // if (toe.error != null) {
    //     //     return toe.error;
    //     // }
    //     String body = mapper.writeValueAsString(toe.UCSBSubject);
    //     return ResponseEntity.ok().body(body);
    // }

    // @ApiOperation(value = "Get a single UCSBSubject (no matter who it belongs to, admin only)")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @GetMapping("/admin")
    // public ResponseEntity<String> getUCSBSubjectById_admin(
    //         @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
    //     loggingService.logMethod();

    //     UCSBSubjectOrError toe = new UCSBSubjectOrError(id);

    //     toe = doesUCSBSubjectExist(toe);
    //     if (toe.error != null) {
    //         return toe.error;
    //     }

    //     String body = mapper.writeValueAsString(toe.UCSBSubject);
    //     return ResponseEntity.ok().body(body);
    // }

    @ApiOperation(value = "Create a new UCSBSubject")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBSubject postUCSBSubject(
        @ApiParam("subjectCode") @RequestParam String subjectCode,
        @ApiParam("subjectTranslation") @RequestParam String subjectTranslation,
        @ApiParam("deptCode") @RequestParam String deptCode,
        @ApiParam("collegeCode") @RequestParam String collegeCode,
        @ApiParam("relatedDeptCode") @RequestParam String relatedDeptCode,
        @ApiParam("inactive") @RequestParam boolean inactive) {
        loggingService.logMethod();
   
        UCSBSubject UCSBSubject = new UCSBSubject();
        //UCSBSubject.setUser(currentUser.getUser());
        UCSBSubject.setSubjectCode(subjectCode);
        UCSBSubject.setSubjectTranslation(subjectTranslation);
        UCSBSubject.setDeptCode(deptCode);
        UCSBSubject.setCollegeCode(collegeCode);
        UCSBSubject.setRelatedDeptCode(relatedDeptCode);
        UCSBSubject.setInactive(inactive);
        UCSBSubject savedUCSBSubject = UCSBSubjectRepository.save(UCSBSubject);
        return savedUCSBSubject;
    }

    // @ApiOperation(value = "Delete another user's todo")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @DeleteMapping("/admin")
    // public ResponseEntity<String> deleteTodo_Admin(
    //         @ApiParam("id") @RequestParam Long id) {
    //     loggingService.logMethod();

    //     TodoOrError toe = new TodoOrError(id);

    //     toe = doesTodoExist(toe);
    //     if (toe.error != null) {
    //         return toe.error;
    //     }

    //     todoRepository.deleteById(id);

    //     return ResponseEntity.ok().body(String.format("todo with id %d deleted", id));

    // }

    // @ApiOperation(value = "Update a single todo (if it belongs to current user)")
    // @PreAuthorize("hasRole('ROLE_USER')")
    // @PutMapping("")
    // public ResponseEntity<String> putTodoById(
    //         @ApiParam("id") @RequestParam Long id,
    //         @RequestBody @Valid Todo incomingTodo) throws JsonProcessingException {
    //     loggingService.logMethod();

    //     CurrentUser currentUser = getCurrentUser();
    //     User user = currentUser.getUser();

    //     TodoOrError toe = new TodoOrError(id);

    //     toe = doesTodoExist(toe);
    //     if (toe.error != null) {
    //         return toe.error;
    //     }
    //     toe = doesTodoBelongToCurrentUser(toe);
    //     if (toe.error != null) {
    //         return toe.error;
    //     }

    //     incomingTodo.setUser(user);
    //     todoRepository.save(incomingTodo);

    //     String body = mapper.writeValueAsString(incomingTodo);
    //     return ResponseEntity.ok().body(body);
    // }

    // @ApiOperation(value = "Update a single todo (regardless of ownership, admin only, can't change ownership)")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @PutMapping("/admin")
    // public ResponseEntity<String> putTodoById_admin(
    //         @ApiParam("id") @RequestParam Long id,
    //         @RequestBody @Valid Todo incomingTodo) throws JsonProcessingException {
    //     loggingService.logMethod();

    //     TodoOrError toe = new TodoOrError(id);

    //     toe = doesTodoExist(toe);
    //     if (toe.error != null) {
    //         return toe.error;
    //     }

    //     // Even the admin can't change the user; they can change other details
    //     // but not that.

    //     User previousUser = toe.todo.getUser();
    //     incomingTodo.setUser(previousUser);
    //     todoRepository.save(incomingTodo);

    //     String body = mapper.writeValueAsString(incomingTodo);
    //     return ResponseEntity.ok().body(body);
    // }
    @ApiOperation(value = "Delete a UCSBSubject owned by this user")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteUCSBSubject(
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        UCSBSubjectOrError toe = new UCSBSubjectOrError(id);

        toe = doesUCSBSubjectExist(toe);
        if (toe.error != null) {
            return ResponseEntity.badRequest().body(String.format("record %d not found", id));
        }

        toe = doesUCSBSubjectBelongToCurrentUser(toe);
        if (toe.error != null) {
            return ResponseEntity.badRequest().body(String.format("record %d not found", id));
        }
        UCSBSubjectRepository.deleteById(id);
        return ResponseEntity.ok().body(String.format("record %d deleted", id));

    }

    @ApiOperation(value = "Delete another user's UCSBSubject")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin")
    public ResponseEntity<String> deleteUCSBSubject_Admin(
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        UCSBSubjectOrError toe = new UCSBSubjectOrError(id);

        toe = doesUCSBSubjectExist(toe);
        if (toe.error != null) {
            return ResponseEntity.badRequest().body(String.format("record %d not found", id));
        }

        UCSBSubjectRepository.deleteById(id);

        return ResponseEntity.ok().body(String.format("record %d deleted", id));

    }
    /**
     * Pre-conditions: toe.id is value to look up, toe.todo and toe.error are null
     * 
     * Post-condition: if todo with id toe.id exists, toe.todo now refers to it, and
     * error is null.
     * Otherwise, todo with id toe.id does not exist, and error is a suitable return
     * value to
     * report this error condition.
     */
     public UCSBSubjectOrError doesUCSBSubjectExist(UCSBSubjectOrError toe) {

         Optional<UCSBSubject> optionalUCSBSubject = UCSBSubjectRepository.findById(toe.id);

         if (optionalUCSBSubject.isEmpty()) {
             toe.error = ResponseEntity
                     .badRequest()
                     .body(String.format("UCSBSubject with id %d not found", toe.id));
         } else {
             toe.UCSBSubject = optionalUCSBSubject.get();
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
     public UCSBSubjectOrError doesUCSBSubjectBelongToCurrentUser(UCSBSubjectOrError toe) {
         CurrentUser currentUser = getCurrentUser();
         log.info("currentUser={}", currentUser);

         
         Long currentUserId = currentUser.getUser().getId();
         Long UCSBSubjectUserId = toe.UCSBSubject.getId();
         log.info("currentUserId={} UCSBSubjectUserId={}", currentUserId, UCSBSubjectUserId);

         if (UCSBSubjectUserId != currentUserId) {
             toe.error = ResponseEntity
                     .badRequest()
                     .body(String.format("UCSBSubject with id %d not found", toe.id));
         }
         return toe;
     }

}
