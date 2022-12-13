package com.nashtech.assignment.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.assignment.AssignmentApplication;
import com.nashtech.assignment.config.CORSConfig;
import com.nashtech.assignment.config.SecurityConfig;
import com.nashtech.assignment.dto.request.category.CreateNewCategoryRequest;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.category.CategoryService;
import com.nashtech.assignment.utils.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@WebMvcTest(value = CategoryController.class)
@ContextConfiguration(classes = {AssignmentApplication.class, SecurityConfig.class, CORSConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CategoryService categoryService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private SecurityContextService securityContextService;

    private CategoryResponse categoryResponse;
    private BadRequestException badRequestException;

    @BeforeEach
    void setup() {
        badRequestException = new BadRequestException("error message");
        categoryResponse = CategoryResponse.builder()
                .id(1)
                .name("name")
                .prefixAssetCode("prefix").build();
    }

    @Test
    void getAllCategories_ShouldReturnData() throws Exception {
        List<CategoryResponse> response = new ArrayList<>();
        response.add(categoryResponse);

        when(categoryService.getAllCategories()).thenReturn(response);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/category");
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.OK.value()));
        assertThat(actual.getContentAsString(), is("[{\"id\":1,\"name\":\"name\",\"prefixAssetCode\":\"prefix\"}]"));
    }

    @Test
    void createNewCategory_ShouldReturnData() throws Exception {
        CreateNewCategoryRequest request = CreateNewCategoryRequest
                .builder()
                .categoryName("categoryName")
                .prefixAssetCode("CN")
                .build();

        ArgumentCaptor<CreateNewCategoryRequest> categoryCaptor = ArgumentCaptor
                .forClass(CreateNewCategoryRequest.class);

        CategoryResponse response = CategoryResponse
                .builder()
                .name("categoryName")
                .prefixAssetCode("CN")
                .build();

        when(categoryService.createNewCategory(categoryCaptor.capture())).thenReturn(response);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/category")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();
        assertThat(actual.getContentAsString(), is("{\"id\":0,\"name\":\"categoryName\",\"prefixAssetCode\":\"CN\"}"));
    }

    @Test
    void createNewCategory_WhenDataInvalid_ShouldReturnException() throws Exception {
        CreateNewCategoryRequest request = CreateNewCategoryRequest
                .builder()
                .categoryName("categoryName")
                .prefixAssetCode("CN")
                .build();

        when(categoryService.createNewCategory(request)).thenThrow(badRequestException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/category");
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();
        assertThat(actual.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }
}