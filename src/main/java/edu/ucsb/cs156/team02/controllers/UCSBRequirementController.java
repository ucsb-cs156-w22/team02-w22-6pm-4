package edu.ucsb.cs156.team02.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

@Api(description = "An endpoint for information about UCSB degree requirements.")
@RequestMapping("/api/UCSBRequirements")
@RestController
@Slf4j
public class UCSBRequirementController extends ApiController
{
    @Autowired
    UCSBRequirementRepository repository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all degree requirements.")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBRequirement> getAllRequirements() {
        loggingService.logMethod();
        Iterable<UCSBRequirement> requirements = repository.findAll();
        return requirements;
    }

    @ApiOperation(value = "Create a new degree requirement.")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBRequirement postRequirement(
        @ApiParam("Requirement code.") @RequestParam String requirementCode,
        @ApiParam("Requirement translation.") @RequestParam String requirementTranslation,
        @ApiParam("College code.") @RequestParam String collegeCode,
        @ApiParam("Objective code.") @RequestParam String objCode,
        @ApiParam("Course count.") @RequestParam int courseCount,
        @ApiParam("Units.") @RequestParam int units,
        @ApiParam("Inactive?") @RequestParam Boolean inactive)
    {
        loggingService.logMethod();
        UCSBRequirement requirement = new UCSBRequirement();
        requirement.setRequirementCode(requirementCode);
        requirement.setRequirementTranslation(requirementTranslation);
        requirement.setCollegeCode(collegeCode);
        requirement.setObjCode(objCode);
        requirement.setCourseCount(courseCount);
        requirement.setUnits(units);
        requirement.setInactive(inactive);
        UCSBRequirement saved = repository.save(requirement);
        return saved;
    }

    @ApiOperation(value = "Get a degree requirement.")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getRequirement(
        @ApiParam("The requirement's unique identifier.") @RequestParam Long id)
        throws JsonProcessingException
    {
        loggingService.logMethod();

        Optional<UCSBRequirement> requirement = repository.findById(id);

        if (requirement.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(String.format("id %d not found", id));
        }

        String body = mapper.writeValueAsString(requirement);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Update a degree requirement.")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public ResponseEntity<String> putRequirement(
        @ApiParam("id") @RequestParam Long id,
        @RequestBody @Valid UCSBRequirement edited)
        throws JsonProcessingException
    {
        loggingService.logMethod();

        Optional<UCSBRequirement> requirement = repository.findById(id);

        if (requirement.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(String.format("id %d not found", id));
        }

        // Overwrite the requirement with the requirement in the body of the request.
        repository.save(edited);

        String body = mapper.writeValueAsString(edited);
        return ResponseEntity.ok().body(body);
    }
}
