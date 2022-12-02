package com.nashtech.assignment.controllers;

import static org.mockito.Mockito.when;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.nashtech.assignment.AssignmentApplication;
import com.nashtech.assignment.config.CORSConfig;
import com.nashtech.assignment.config.SecurityConfig;
import com.nashtech.assignment.services.SecurityContextService;
import com.nashtech.assignment.utils.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nashtech.assignment.data.constants.EGender;
import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.FindService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(FindController.class)
@ContextConfiguration(classes = {AssignmentApplication.class, SecurityConfig.class, CORSConfig.class})
@AutoConfigureMockMvc(addFilters = false)
public class FindControllerTest {

    @MockBean
    private FindService findService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private SecurityContextService securityContextService;
    private UserResponse userResponse;

    @BeforeEach
    void setup() {
        userResponse = UserResponse.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .gender(EGender.OTHERS)
                .staffCode("staffCode")
                .username("Username")
                .location("test").build();
    }

    @Test
    void filterByType_ShouldReturnPaginationResponse() throws Exception {
        List<UserResponse> users = new ArrayList<>();
        users.add(userResponse);
        PaginationResponse<List<UserResponse>> test = new PaginationResponse<List<UserResponse>>(users, 1, 1);
        when(findService.filterByType(EUserType.ADMIN, 1, "HCM")).thenReturn(test);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/find/filter/{page}", 1)
                .param("type", "ADMIN")
                .param("location", "HCM");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getContentAsString(), is("{\"data\":[{\"username\":\"Username\",\"staffCode\":\"staffCode\",\"firstName\":\"First Name\",\"lastName\":\"Last Name\",\"gender\":\"OTHERS\",\"joinedDate\":null,\"dateOfBirth\":null,\"type\":null,\"location\":\"test\",\"fullName\":null}],\"totalPage\":1,\"totalRow\":1}"));
    }

    @Test
    void filterByType_ShouldReturnEmptyList() throws Exception {
        List<UserResponse> users = new ArrayList<>();
        PaginationResponse<List<UserResponse>> test = new PaginationResponse<List<UserResponse>>(users, 1, 1);
        when(findService.filterByType(EUserType.ADMIN, 1, "HCM")).thenReturn(test);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/find/filter/{page}", 1)
                .param("type", "ADMIN")
                .param("location", "HCM");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getContentAsString(), is("{\"data\":[],\"totalPage\":1,\"totalRow\":1}"));
    }


    @Test
    void findByLocation_ShouldReturnPaginationResponse() throws Exception {
        List<UserResponse> users = new ArrayList<>();
        users.add(userResponse);
        PaginationResponse<List<UserResponse>> test = new PaginationResponse<List<UserResponse>>(users, 1, 1);

        when(findService.findByLocation("Location", 1)).thenReturn(test);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/find")
                .param("pageNumber", String.valueOf(1))
                .param("location", "Location");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getContentAsString(),
                is("{\"data\":[{\"username\":\"Username\",\"staffCode\":\"staffCode\",\"firstName\":\"First Name\",\"lastName\":\"Last Name\",\"gender\":\"OTHERS\",\"joinedDate\":null,\"dateOfBirth\":null,\"type\":null,\"location\":\"test\",\"fullName\":null}],\"totalPage\":1,\"totalRow\":1}"));
    }

    @Test
    void search_ShouldReturnPagination() throws Exception {
        List<UserResponse> users = new ArrayList<>();
        users.add(userResponse);
        PaginationResponse<List<UserResponse>> test = new PaginationResponse<List<UserResponse>>(users, 1, 1);

        when(findService.searchByNameOrStaffCodeAndFilterByTypeAndLocation(1, "name", "staff code", EUserType.STAFF, "location")).thenReturn(test);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/find/search")
                .param("name", "name")
                .param("type", "STAFF")
                .param("location", "location")
                .param("page", "1")
                .param("staffCode", "staff code");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getContentAsString(),
                is("{\"data\":[{\"username\":\"Username\",\"staffCode\":\"staffCode\",\"firstName\":\"First Name\",\"lastName\":\"Last Name\",\"gender\":\"OTHERS\",\"joinedDate\":null,\"dateOfBirth\":null,\"type\":null,\"location\":\"test\",\"fullName\":null}],\"totalPage\":1,\"totalRow\":1}"));
    }

    @Test
    void viewUserDetails_ShouldReturnUserResponse() throws Exception {
        when(findService.viewUserDetails("test")).thenReturn(userResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/find/get/{staffCode}", "test");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getContentAsString(),
                is("{\"username\":\"Username\",\"staffCode\":\"staffCode\",\"firstName\":\"First Name\",\"lastName\":\"Last Name\",\"gender\":\"OTHERS\",\"joinedDate\":null,\"dateOfBirth\":null,\"type\":null,\"location\":\"test\",\"fullName\":null}"));
    }

    @Test
    void viewUserDetails_ShouldThrowNotFoundException() throws Exception {
        NotFoundException notFoundException = new NotFoundException("Message");

        when(findService.viewUserDetails("test")).thenThrow(notFoundException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/find/get/{staffCode}", "test");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NOT_FOUND.value()));
        assertThat(result.getResponse().getContentAsString(), is("{\"message\":\"Message\"}"));
    }
}
