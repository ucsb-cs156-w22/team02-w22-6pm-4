package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.CollegiateSubreddit;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.CollegiateSubredditRepository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = CollegiateSubredditController.class)
@Import(TestConfig.class)
public class CollegiateSubredditControllerTests extends ControllerTestCase {

    @MockBean
    CollegiateSubredditRepository collegiateSubredditRepository;

    @MockBean
    UserRepository userRepository;

    
    // Authorization tests for /api/todos/all

    @Test
    public void api_collegiateSubreddits_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/collegiateSubreddits/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddits_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/collegiateSubreddits/all"))
                .andExpect(status().isOk());
    }

    // Authorization tests for /api/todos/post

    @Test
    public void api_collegiateSubreddits_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/collegiateSubreddits/post"))
                .andExpect(status().is(403));
    }

    // Tests with mocks for database actions

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddits_post__user_logged_in() throws Exception {
        // arrange

        CollegiateSubreddit expectedCollegiateSubreddit = CollegiateSubreddit.builder()
                .name("Test Name")
                .location("Test Location")
                .subreddit("Test Subreddit")
                .id(0L)
                .build();

        when(collegiateSubredditRepository.save(eq(expectedCollegiateSubreddit))).thenReturn(expectedCollegiateSubreddit);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/collegiateSubreddits/post?name=Test Name&location=Test Location&subreddit=Test Subreddit")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).save(expectedCollegiateSubreddit);
        String expectedJson = mapper.writeValueAsString(expectedCollegiateSubreddit);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }    

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddits_all__user_logged_in__returns_only_collegiateSubreddits_for_user() throws Exception {

        // arrange

        CollegiateSubreddit collegiatesubreddit1 = CollegiateSubreddit.builder().name("collegiatesubreddit 1").location("collegiatesubreddit 1").subreddit("collegiatesubreddit 1").id(1L).build();
        CollegiateSubreddit collegiatesubreddit2 = CollegiateSubreddit.builder().name("collegiatesubreddit 2").location("collegiatesubreddit 2").subreddit("collegiatesubreddit 2").id(2L).build();

        ArrayList<CollegiateSubreddit> expectedCollegiateSubreddit = new ArrayList<>();
        expectedCollegiateSubreddit.addAll(Arrays.asList(collegiatesubreddit1, collegiatesubreddit2));
        when(collegiateSubredditRepository.findAll()).thenReturn(expectedCollegiateSubreddit);

        // act
        MvcResult response = mockMvc.perform(get("/api/collegiateSubreddits/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(collegiateSubredditRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedCollegiateSubreddit);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddits_user_logged_in_returns_a_collegiateSubreddit_that_exists() throws Exception {

        // arrange

        CollegiateSubreddit collegeSubreddit1 = CollegiateSubreddit.builder().name("collegeSubreddit 1").location("collegeSubreddit 1").subreddit("collegeSubreddit 1").id(7L).build();
        when(collegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.of(collegeSubreddit1));

        MvcResult response = mockMvc.perform(get("/api/collegiateSubreddits?id=7"))
                .andExpect(status().isOk()).andReturn();

        verify(collegiateSubredditRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(collegeSubreddit1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddits_user_logged_in_search_for_collegiateSubreddit_that_does_not_exist() throws Exception {

        //arrange

        when(collegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.empty());

        //act
        MvcResult response = mockMvc.perform(get("/api/collegiateSubreddits?id=7"))
                .andExpect(status().isBadRequest()).andReturn();

        //assert

        verify(collegiateSubredditRepository, times(1)).findById(eq(7L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 7 not found", responseString);
    }

     //Test for the getID() method
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddits_return_own_id() throws Exception {
        CollegiateSubreddit collegiateSubreddit1 = CollegiateSubreddit.builder().id(7L).build();

        assertEquals(7L, collegiateSubreddit1.getId());
    }
}
