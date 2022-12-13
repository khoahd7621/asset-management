package com.nashtech.assignment.services.search.impl;

import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.user.SearchUserRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.mappers.UserMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.utils.PageableUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SearchUserServiceImplTest {

    private SearchUserServiceImpl searchUserServiceImpl;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private PageableUtil pageableUtil;
    private SecurityContextService securityContextService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUpTest() {
        userMapper = mock(UserMapper.class);
        userRepository = mock(UserRepository.class);
        pageableUtil = mock(PageableUtil.class);
        securityContextService = mock(SecurityContextService.class);
        searchUserServiceImpl = SearchUserServiceImpl.builder()
                .userRepository(userRepository)
                .userMapper(userMapper)
                .pageableUtil(pageableUtil)
                .securityContextService(securityContextService).build();
        user = mock(User.class);
        userResponse = mock(UserResponse.class);
    }

    @Test
    void searchAllUsersByKeyWordInTypesWithPagination_WhenKeyWordAndTypesNotNull_ShouldReturnData() {
        List<EUserType> types = new ArrayList<>();
        types.add(EUserType.ADMIN);
        SearchUserRequest searchUserRequest = SearchUserRequest.builder()
                .keyword("keyword").types(types).limit(20).page(0)
                .sortField("firstName").sortType("ASC").build();

        Pageable pageable = PageRequest.of(
                searchUserRequest.getPage(),
                searchUserRequest.getLimit(),
                Sort.by(searchUserRequest.getSortField()).ascending());
        ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List<EUserType>> typesCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<String> locationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        Page<User> userPage = new PageImpl<>(userList);
        List<UserResponse> userResponseList = new ArrayList<>();
        userResponseList.add(userResponse);
        PaginationResponse<List<UserResponse>> expectedResponse = PaginationResponse.<List<UserResponse>>builder()
                .data(userResponseList).totalRow(1).totalPage(1).build();

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(pageableUtil.getPageable(
                searchUserRequest.getPage(),
                searchUserRequest.getLimit(),
                searchUserRequest.getSortField(),
                searchUserRequest.getSortType())).thenReturn(pageable);
        when(userRepository.searchAllUsersByKeyWordInTypesWithPagination(
                keywordCaptor.capture(),
                typesCaptor.capture(),
                locationCaptor.capture(),
                pageableCaptor.capture())).thenReturn(userPage);
        when(userMapper.toListUserResponses(userPage.toList())).thenReturn(userResponseList);

        PaginationResponse<List<UserResponse>> actual = searchUserServiceImpl
                .searchAllUsersByKeyWordInTypesWithPagination(searchUserRequest);

        assertThat(pageableCaptor.getValue(), is(pageable));
        assertThat(keywordCaptor.getValue(), is("keyword"));
        assertThat(typesCaptor.getValue(), is(types));
        assertThat(locationCaptor.getValue(), is(user.getLocation()));
        assertThat(actual.getData(), is(expectedResponse.getData()));
        assertThat(actual.getTotalPage(), is(expectedResponse.getTotalPage()));
        assertThat(actual.getTotalRow(), is(expectedResponse.getTotalRow()));
    }

    @Test
    void searchAllUsersByKeyWordInTypesWithPagination_WhenKeyWordOrTypesIsNull_ShouldReturnData() {
        SearchUserRequest searchUserRequest = SearchUserRequest.builder()
                .keyword(null).types(null).limit(20).page(0)
                .sortField("firstName").sortType("ASC").build();

        Pageable pageable = PageRequest.of(
                searchUserRequest.getPage(),
                searchUserRequest.getLimit(),
                Sort.by(searchUserRequest.getSortField()).ascending());
        ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List<EUserType>> typesCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<String> locationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        Page<User> userPage = new PageImpl<>(userList);
        List<UserResponse> userResponseList = new ArrayList<>();
        userResponseList.add(userResponse);
        PaginationResponse<List<UserResponse>> expectedResponse = PaginationResponse.<List<UserResponse>>builder()
                .data(userResponseList).totalRow(1).totalPage(1).build();

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(pageableUtil.getPageable(
                searchUserRequest.getPage(),
                searchUserRequest.getLimit(),
                searchUserRequest.getSortField(),
                searchUserRequest.getSortType())).thenReturn(pageable);
        when(userRepository.searchAllUsersByKeyWordInTypesWithPagination(
                keywordCaptor.capture(),
                typesCaptor.capture(),
                locationCaptor.capture(),
                pageableCaptor.capture())).thenReturn(userPage);
        when(userMapper.toListUserResponses(userPage.toList())).thenReturn(userResponseList);

        PaginationResponse<List<UserResponse>> actual = searchUserServiceImpl
                .searchAllUsersByKeyWordInTypesWithPagination(searchUserRequest);

        assertThat(pageableCaptor.getValue(), is(pageable));
        assertThat(keywordCaptor.getValue(), is(nullValue()));
        assertThat(typesCaptor.getValue(), is(nullValue()));
        assertThat(locationCaptor.getValue(), is(user.getLocation()));
        assertThat(actual.getData(), is(expectedResponse.getData()));
        assertThat(actual.getTotalPage(), is(expectedResponse.getTotalPage()));
        assertThat(actual.getTotalRow(), is(expectedResponse.getTotalRow()));
    }
}