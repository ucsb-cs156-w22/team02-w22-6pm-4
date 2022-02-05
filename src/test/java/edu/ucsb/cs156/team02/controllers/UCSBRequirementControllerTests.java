package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;
import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UCSBRequirementController.class)
@Import(TestConfig.class)
public class UCSBRequirementControllerTests extends ControllerTestCase
{
    @MockBean
    UCSBRequirementRepository repository;

    @MockBean
    UserRepository userRepository;

    /* /api/UCSBRequirements/all */

    @Test
    public void api_requirements_stranger_does_get_all() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_user_does_get_all() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_requirements_admin_does_get_all() throws Exception {
        UCSBRequirement dummy = UCSBRequirement.dummy(0);
        UCSBRequirement another = UCSBRequirement.dummy(1);

        ArrayList<UCSBRequirement> requirements = new ArrayList<>();
        requirements.addAll(Arrays.asList(dummy, another));

        when(repository.findAll()).thenReturn(requirements);

        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements/all"))
            .andExpect(status().isOk()).andReturn();

        verify(repository, times(1)).findAll();

        String expectedJson = mapper.writeValueAsString(requirements);
        String responseString = response.getResponse().getContentAsString();

        assertEquals(expectedJson, responseString);
    }

    /* /api/UCSBRequirements/post */

    final static String somePost = "/api/UCSBRequirements/post?"
        + "requirementCode=AMH"
        + "&requirementTranslation=American History and Institution"
        + "&collegeCode=UCSB"
        + "&objCode=UG"
        + "&courseCount=1"
        + "&units=4"
        + "&inactive=true";

    @Test
    public void api_requirements_stranger_does_post_requirement() throws Exception {
        mockMvc.perform(post(somePost))
            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_user_does_post_requirement() throws Exception {
        UCSBRequirement requirement = UCSBRequirement.dummy(0);

        when(repository.save(eq(requirement))).thenReturn(requirement);

        // There's got to be a better way than manually encoding URL parameters.

        MvcResult response = mockMvc.perform(
                post(somePost)
                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        verify(repository, times(1)).save(requirement);

        String expectedJson = mapper.writeValueAsString(requirement);
        String responseString = response.getResponse().getContentAsString();

        assertEquals(expectedJson, responseString);
    }
}
