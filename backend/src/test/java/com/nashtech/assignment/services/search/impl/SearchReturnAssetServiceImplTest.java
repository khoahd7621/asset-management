package com.nashtech.assignment.services.search.impl;

import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.data.entities.ReturnAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.ReturnAssetRepository;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.returned.ReturnAssetResponse;
import com.nashtech.assignment.mappers.ReturnAssetMapper;
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

class SearchReturnAssetServiceImplTest {
    private SearchReturnAssetServiceImpl searchReturnAssetServiceImpl;
    private ReturnAssetRepository returnAssetRepository;
    private ReturnAssetMapper returnAssetMapper;
    private SecurityContextService securityContextService;
    private User user;

    @BeforeEach
    void setUp() {
        returnAssetRepository = mock(ReturnAssetRepository.class);
        returnAssetMapper = mock(ReturnAssetMapper.class);
        securityContextService = mock(SecurityContextService.class);
        searchReturnAssetServiceImpl = SearchReturnAssetServiceImpl.builder()
                .returnAssetRepository(returnAssetRepository)
                .returnAssetMapper(returnAssetMapper)
                .securityContextService(securityContextService).build();
        user = mock(User.class);
    }

    @Test
    void searchAllReturnAssetsByStateAndReturnedDateAndSearchWithPagination_WhenDataValid_ShouldReturnPaginationObject()
            throws ParseException {
        String name = "test";
        String date = "29/11/2022";
        List<EReturnStatus> eReturnStatus = new ArrayList<>();
        eReturnStatus.add(EReturnStatus.COMPLETED);
        eReturnStatus.add(EReturnStatus.WAITING_FOR_RETURNING);
        Pageable page = PageRequest.of(0, 19);
        Page<ReturnAsset> returnAssetPage = mock(Page.class);
        List<ReturnAssetResponse> returnAssetResponses = mock(List.class);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(returnAssetRepository.searchAllReturnAssetsByStateAndReturnedDateAndSearchWithPagination(name,
                eReturnStatus, date, user.getLocation(), page))
                .thenReturn(returnAssetPage);
        when(returnAssetMapper.mapListEntityReturnAssetResponses(returnAssetPage.getContent()))
                .thenReturn(returnAssetResponses);

        PaginationResponse<List<ReturnAssetResponse>> acutal = searchReturnAssetServiceImpl
                .searchAllReturnAssetsByStateAndReturnedDateAndSearchWithPagination(name, eReturnStatus,
                        date, 0);

        assertThat(acutal.getData(), is(returnAssetResponses));
    }

    @Test
    void searchAllReturnAssetsByStateAndReturnedDateAndSearchWithPagination_WhenDataEmpty_ShouldReturnEmptyColection()
            throws ParseException {
        String name = "test";
        String date = "29/11/2022";
        List<EReturnStatus> eReturnStatus = new ArrayList<>();
        eReturnStatus.add(EReturnStatus.COMPLETED);
        eReturnStatus.add(EReturnStatus.WAITING_FOR_RETURNING);
        Pageable page = PageRequest.of(0, 19);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(returnAssetRepository.searchAllReturnAssetsByStateAndReturnedDateAndSearchWithPagination(name,
                eReturnStatus, date, user.getLocation(), page)).thenReturn(null);

        PaginationResponse<List<ReturnAssetResponse>> acutal = searchReturnAssetServiceImpl
                .searchAllReturnAssetsByStateAndReturnedDateAndSearchWithPagination(name, eReturnStatus,
                        date, 0);

        assertThat(acutal.getData(), is(Collections.emptyList()));
    }
}
