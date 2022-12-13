package com.nashtech.assignment.services.search.impl;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SearchAssignAssetServiceImplTest {
    private SearchAssignAssetServiceImpl searchAssignAssetServiceImpl;
    private AssignAssetRepository assignAssetRepository;
    private AssignAssetMapper assignAssetMapper;
    private SecurityContextService securityContextService;

    private User user;

    @BeforeEach
    void setup() {
        assignAssetRepository = mock(AssignAssetRepository.class);
        assignAssetMapper = mock(AssignAssetMapper.class);
        securityContextService = mock(SecurityContextService.class);
        searchAssignAssetServiceImpl = SearchAssignAssetServiceImpl.builder()
                .assignAssetRepository(assignAssetRepository)
                .assignAssetMapper(assignAssetMapper)
                .securityContextService(securityContextService).build();
        user = mock(User.class);
    }

    @Test
    void filterAndSearchAssignAsset_WhenDataValid_ShouldReturnPaginationObject()
            throws ParseException {
        String name = "test";
        String date = "29/11/2022";
        List<EAssignStatus> eAssignStatus = new ArrayList<>();
        eAssignStatus.add(EAssignStatus.ACCEPTED);
        eAssignStatus.add(EAssignStatus.WAITING_FOR_ACCEPTANCE);
        Pageable page = PageRequest.of(0, 19);
        Page<AssignAsset> assignAssetPage = mock(Page.class);
        List<AssignAssetResponse> assignAssetResponses = mock(List.class);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.searchByNameOrStatusOrDateAndLocation(name,
                eAssignStatus, date, user.getLocation(), page))
                .thenReturn(assignAssetPage);
        when(assignAssetMapper.toListAssignAssetResponses(assignAssetPage.getContent()))
                .thenReturn(assignAssetResponses);

        PaginationResponse<List<AssignAssetResponse>> acutal = searchAssignAssetServiceImpl
                .filterAndSearchAssignAsset(name, eAssignStatus, date, 0);

        assertThat(acutal.getData(), is(assignAssetResponses));
    }

    @Test
    void filterAndSearchAssignAsset_WhenDataEmpty_ShouldReturnEmptyColection()
            throws ParseException {
        String name = "test";
        String date = "29/11/2022";
        List<EAssignStatus> eAssignStatus = new ArrayList<>();
        eAssignStatus.add(EAssignStatus.ACCEPTED);
        eAssignStatus.add(EAssignStatus.WAITING_FOR_ACCEPTANCE);
        Pageable page = PageRequest.of(0, 19);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.searchByNameOrStatusOrDateAndLocation(name,
                eAssignStatus, date, user.getLocation(), page)).thenReturn(null);

        PaginationResponse<List<AssignAssetResponse>> acutal = searchAssignAssetServiceImpl
                .filterAndSearchAssignAsset(name, eAssignStatus, date, 0);

        assertThat(acutal.getData(), is(Collections.emptyList()));
    }
}
