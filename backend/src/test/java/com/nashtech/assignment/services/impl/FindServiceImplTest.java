package com.nashtech.assignment.services.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.UserMapper;


public class FindServiceImplTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private FindServiceImpl findServiceImpl;
    
    @BeforeEach
    void setUpTest(){
        userMapper = mock(UserMapper.class);
        userRepository = mock(UserRepository.class);
        findServiceImpl = new FindServiceImpl(userRepository, userMapper);
    }

    @Test
    void filterByType_WhenDataValid_ShouldReturnPaginationResponse() {
        Pageable pageable = PageRequest.of(0, 20,Sort.by("firstName"));
        Page<User> page = mock(Page.class);
        List<UserResponse> users = mock(List.class);

        when(userRepository.findByLocationAndTypeAndIsDeletedFalseOrderByFirstNameAsc("HCM",EUserType.ADMIN,pageable)).thenReturn(page);
        when(userMapper.mapListEntityUserResponses(page.getContent())).thenReturn(users);
        
        PaginationResponse<List<UserResponse>> actual = findServiceImpl.filterByType(EUserType.ADMIN, 0,"HCM");

        assertThat(actual.getData(), is(users));
    }
    @Test
    void filterByType_WhenDataValid_ShouldReturnEmptyCollection() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> page = mock(Page.class);
        List<UserResponse> users = mock(List.class);

        when(userRepository.findByLocationAndTypeAndIsDeletedFalseOrderByFirstNameAsc("HCM",EUserType.ADMIN,pageable)).thenReturn(null);
        when(userMapper.mapListEntityUserResponses(page.getContent())).thenReturn(users);
        
        PaginationResponse<List<UserResponse>> actual = findServiceImpl.filterByType(EUserType.ADMIN, 0,"HCM");

        assertThat(actual.getData(), is(Collections.emptyList()));
    }

    @Test
    void filterByType_WhenTypeAll_ShouldReturnPaginationResponse() {
        Pageable pageable = PageRequest.of(0, 20,Sort.by("firstName"));

        Page<User> page = mock(Page.class);
        List<UserResponse> users = mock(List.class);

        when(userRepository.findByLocationAndIsDeletedFalseOrderByFirstNameAsc("HCM",pageable)).thenReturn(page);
        when(userMapper.mapListEntityUserResponses(page.getContent())).thenReturn(users);

        PaginationResponse<List<UserResponse>> actual = findServiceImpl.filterByType(null, 0,"HCM");

        assertThat(actual.getData(), is(users));
    }
    @Test
    void filterByType_WhenTypeAll_ShouldReturnEmpty() {
        Pageable pageable = PageRequest.of(0, 20,Sort.by("firstName"));

        Page<User> page = mock(Page.class);
        List<UserResponse> users = mock(List.class);

        when(userRepository.findByLocationAndIsDeletedFalseOrderByFirstNameAsc("HCM",pageable)).thenReturn(null);
        when(userMapper.mapListEntityUserResponses(page.getContent())).thenReturn(users);

        PaginationResponse<List<UserResponse>> actual = findServiceImpl.filterByType(null, 0,"HCM");

        assertThat(actual.getData(), is(Collections.emptyList()));
    }

    @Test
    void findByLocation_WhenDataValid_ShouldReturnPainationResponse() {
        Pageable page = PageRequest.of(0, 20);
        Page<User> pages = mock(Page.class);
        List<UserResponse> expected = mock(List.class);

        when(userRepository.findByLocationAndIsDeletedFalseOrderByFirstNameAsc("test", page)).thenReturn(pages);
        when(userMapper.mapListEntityUserResponses(pages.getContent())).thenReturn(expected);

        PaginationResponse<List<UserResponse>> actual = findServiceImpl.findByLocation("test", 0);

        assertThat(actual.getData(), is(expected));

    }
    @Test
    void findByLocation_WhenDataNull_ShouldReturnEmptyCollections() {
        Pageable page = PageRequest.of(0, 20);
        Page<User> pages = mock(Page.class);
        List<UserResponse> expected = mock(List.class);

        when(userRepository.findByLocationAndIsDeletedFalseOrderByFirstNameAsc("test", page)).thenReturn(null);
        when(userMapper.mapListEntityUserResponses(pages.getContent())).thenReturn(expected);

        PaginationResponse<List<UserResponse>> actual = findServiceImpl.findByLocation("test", 0);

        assertThat(actual.getData(), is(Collections.emptyList()));

    }
    @Test
    void findByLocation_WhenDataisEmpty_ShouldReturnEmptyCollections() {
        Pageable page = PageRequest.of(0, 20);
        Page<User> pages = new PageImpl<>(Collections.emptyList());
        List<UserResponse> expected = mock(List.class);

        when(userRepository.findByLocationAndIsDeletedFalseOrderByFirstNameAsc("test", page)).thenReturn(pages);
        when(userMapper.mapListEntityUserResponses(pages.getContent())).thenReturn(expected);

        PaginationResponse<List<UserResponse>> actual = findServiceImpl.findByLocation("test", 0);

        assertThat(actual.getData(), is(Collections.emptyList()));

    }

    @Test
    void searchByNameOrStaffCodeAndFilterByTypeAndLocation_WhenDataValid_ShouldReturnList() {
        List<UserResponse> expected = mock(List.class);
        Pageable page = PageRequest.of(0, 20);
        Page<User> pages = mock(Page.class);

        when(userRepository.search("%test%", "test", "test", EUserType.ADMIN, page)).thenReturn(pages);
        when(userMapper.mapListEntityUserResponses(pages.getContent())).thenReturn(expected);

        PaginationResponse<List<UserResponse>> actual = findServiceImpl.searchByNameOrStaffCodeAndFilterByTypeAndLocation(0,"test", "test", EUserType.ADMIN, "test");
        assertThat(actual.getData(), is(expected));
    }
    @Test
    void searchByNameOrStaffCodeAndFilterByTypeAndLocation_WhenStaffNull_ShouldReturnList() {
        List<UserResponse> expected = mock(List.class);
        Pageable page = PageRequest.of(0, 20);
        Page<User> pages = mock(Page.class);

        when(userRepository.search("%test%", "test", "test",null, page)).thenReturn(pages);
        when(userMapper.mapListEntityUserResponses(pages.getContent())).thenReturn(expected);

        PaginationResponse<List<UserResponse>> actual = findServiceImpl.searchByNameOrStaffCodeAndFilterByTypeAndLocation(0,"test", "test", null, "test");
        assertThat(actual.getData(), is(expected));
    }
    @Test
    void searchByNameOrStaffCodeAndFilterByTypeAndLocation_WhenDataNotExist_ShouldReturnEmpty() {
        List<UserResponse> expected = mock(List.class);
        Page<User> pages = new PageImpl<>(Collections.emptyList());
        Pageable page = PageRequest.of(0, 20);
        when(userRepository.search("test", "test", "test", null, page)).thenReturn(pages);
        when(userMapper.mapListEntityUserResponses(pages.getContent())).thenReturn(expected);

        PaginationResponse<List<UserResponse>> actual = findServiceImpl.searchByNameOrStaffCodeAndFilterByTypeAndLocation(0, "test", "test", null, "test");
        assertThat(actual.getData(), is(Collections.emptyList()));
    }
    @Test
    void searchByNameOrStaffCodeAndFilterByTypeAndLocation_WhenDataNull_ShouldReturnEmpty() {
        List<UserResponse> expected = mock(List.class);
        Pageable page = PageRequest.of(0, 20);
        Page<User> pages = new PageImpl<>(Collections.emptyList());
        when(userRepository.search("test", "test", "test", null, page)).thenReturn(pages);
        when(userMapper.mapListEntityUserResponses(pages.getContent())).thenReturn(expected);

        PaginationResponse<List<UserResponse>> actual = findServiceImpl.searchByNameOrStaffCodeAndFilterByTypeAndLocation(0,"test", "test", null, "test");
        assertThat(actual.getData(), is(Collections.emptyList()));
    }

    @Test
    void viewUserDetails_WhenDataValid_ShouldReturnUserDetails() {
        User user = mock(User.class);
        UserResponse userResponse = mock(UserResponse.class);

        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(userMapper.mapEntityToResponseDto(user)).thenReturn(userResponse);

        UserResponse actual = findServiceImpl.viewUserDetails("test");

        assertThat(actual, is(userResponse));

    }
    @Test
    void viewUserDetails_WhenNotFound_ShouldReturnNull() {
        User user = mock(User.class);
        UserResponse userResponse = mock(UserResponse.class);

        when(userRepository.findByStaffCode("test")).thenReturn(null);
        when(userMapper.mapEntityToResponseDto(user)).thenReturn(userResponse);

        NotFoundException actual = assertThrows(NotFoundException.class, ()->findServiceImpl.viewUserDetails("test"));

        assertThat(actual.getMessage(), is("Cannot Found Staff With Code: "+"test"));

    }

}
