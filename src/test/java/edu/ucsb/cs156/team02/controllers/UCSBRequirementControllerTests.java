package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;
import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
    public void api_requirements_stranger_does_get_all() throws Exception
    {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_user_does_get_all() throws Exception
    {
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

    // There's got to be a better way than manually encoding URL parameters.
    // Can't I just tell my dummy to encode itself? It's an Object.

    final static String somePost = "/api/UCSBRequirements/post?"
        + "requirementCode=AMH"
        + "&requirementTranslation=American History and Institution"
        + "&collegeCode=UCSB"
        + "&objCode=UG"
        + "&courseCount=1"
        + "&units=4"
        + "&inactive=true";

    @Test
    public void api_requirements_stranger_does_post_requirement() throws Exception
    {
        mockMvc.perform(post(somePost))
            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_user_does_post_requirement() throws Exception
    {
        UCSBRequirement requirement = UCSBRequirement.dummy(0);

        when(repository.save(eq(requirement))).thenReturn(requirement);

        MvcResult response = mockMvc.perform(
                post(somePost)
                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        verify(repository, times(1)).save(requirement);

        String expectedJson = mapper.writeValueAsString(requirement);
        String responseString = response.getResponse().getContentAsString();

        assertEquals(expectedJson, responseString);
    }

    /* /api/UCSBRequirements?id= */

    @Test
    public void api_requirements_stranger_does_get() throws Exception
    {
        mockMvc.perform(get("/api/UCSBRequirements?id=0"))
            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_user_does_successful_get() throws Exception
    {
        UCSBRequirement requirement = UCSBRequirement.dummy(42L);

        when(repository.findById(eq(42L))).thenReturn(Optional.of(requirement));

        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=42"))
            .andExpect(status().isOk()).andReturn();

        verify(repository, times(1)).findById(eq(42L));

        String expectedJson = mapper.writeValueAsString(requirement);
        String responseString = response.getResponse().getContentAsString();

        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_user_does_unsuccessful_get() throws Exception
    {
        when(repository.findById(eq(42L))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=42"))
            .andExpect(status().isBadRequest()).andReturn();

        verify(repository, times(1)).findById(eq(42L));

        String expectedJson = "id 42 not found";
        String responseString = response.getResponse().getContentAsString();

        assertEquals(expectedJson, responseString);
    }

    /* PUT /api/UCSBRequirements?id= */

    @Test
    public void api_requirements_stranger_does_put() throws Exception
    {
        UCSBRequirement requirement = UCSBRequirement.dummy(42);

        String requestBody = mapper.writeValueAsString(requirement);

        mockMvc.perform(
            put("/api/UCSBRequirements?id=0")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("utf-8")
            .content(requestBody)
            .with(csrf()) // <- ????
        )
        .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_user_does_put() throws Exception
    {
        UCSBRequirement requirement = UCSBRequirement.dummy(42);

        when(repository.findById(eq(42L))).thenReturn(Optional.of(requirement));

        UCSBRequirement edited = UCSBRequirement.dummy(42);
        edited.setUnits(24);

        when(repository.save(eq(edited))).thenReturn(edited);

        String requestBody = mapper.writeValueAsString(edited);

        MvcResult response = mockMvc.perform(
            put("/api/UCSBRequirements?id=42")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("utf-8")
            .content(requestBody)
            .with(csrf()) // <- ????
        )
        .andExpect(status().is(200))
        .andReturn();

        verify(repository, times(1)).findById(42L);
        verify(repository, times(1)).save(edited);

        String responseString = response.getResponse().getContentAsString();
        assertEquals(requestBody, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_user_does_put_with_wrong_id() throws Exception
    {
        when(repository.findById(eq(41L))).thenReturn(Optional.empty());

        UCSBRequirement edited = UCSBRequirement.dummy(42);
        edited.setUnits(24);

        String requestBody = mapper.writeValueAsString(edited);

        MvcResult response = mockMvc.perform(
            put("/api/UCSBRequirements?id=41")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("utf-8")
            .content(requestBody)
            .with(csrf()) // <- ????
        )
        .andExpect(status().isBadRequest()).andReturn();

        String expectedJson = "id 41 not found";
        String responseString = response.getResponse().getContentAsString();

        assertEquals(expectedJson, responseString);
    }

    /* DELETE /api/UCSBRequirements?id= */

    @Test
    public void api_requirements_stranger_does_delete() throws Exception
    {
        mockMvc.perform(
            delete("/api/UCSBRequirements?id=42")
            .with(csrf()) // <- ????
        )
        .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_user_does_good_delete() throws Exception
    {
        UCSBRequirement requirement = UCSBRequirement.dummy(42L);

        when(repository.findById(eq(42L))).thenReturn(Optional.of(requirement));

        doNothing().when(repository).deleteById(42L);

        MvcResult response = mockMvc.perform(
            delete("/api/UCSBRequirements?id=42")
            .with(csrf()) // <- ????
        )
        .andExpect(status().is(200)).andReturn();

        verify(repository, times(1)).findById(42L);
        verify(repository, times(1)).deleteById(42L);

        String responseString = response.getResponse().getContentAsString();
        assertEquals("record 42 deleted", responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_user_does_bad_delete() throws Exception
    {
        when(repository.findById(eq(42L))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(
            delete("/api/UCSBRequirements?id=42")
            .with(csrf()) // <- ????
        )
        .andExpect(status().is(400)).andReturn();

        verify(repository, times(1)).findById(42L);

        String responseString = response.getResponse().getContentAsString();
        assertEquals("record 42 not found", responseString);
    }
}
