package com.nashtech.assignment.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.nashtech.assignment.AssignmentApplication;
import com.nashtech.assignment.config.CORSConfig;
import com.nashtech.assignment.config.SecurityConfig;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.services.GetService;
import com.nashtech.assignment.services.SecurityContextService;
import com.nashtech.assignment.utils.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(value = CategoryController.class)
@ContextConfiguration(classes = { AssignmentApplication.class, SecurityConfig.class, CORSConfig.class })
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GetService getService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private SecurityContextService securityContextService;

    private CategoryResponse categoryResponse;

    @BeforeEach
    void setup() {
        categoryResponse = CategoryResponse.builder()
                .id(1)
                .name("name")
                .prefixAssetCode("prefix").build();
    }

    @Test
    void getAllCategories_ShouldReturnData() throws Exception {
        List<CategoryResponse> response = new ArrayList<>();
        response.add(categoryResponse);

        when(getService.getAllCategories()).thenReturn(response);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/category");
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.OK.value()));
        assertThat(actual.getContentAsString(), is("[{\"id\":1,\"name\":\"name\",\"prefixAssetCode\":\"prefix\"}]"));
    }
}