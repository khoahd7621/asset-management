package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetAssignAssetServiceImplTest {

    private GetAssignAssetServiceImpl getAssignAssetServiceImpl;
    private AssignAssetRepository assignAssetRepository;
    private AssignAssetMapper assignAssetMapper;
    private SecurityContextService securityContextService;

    private AssignAsset assignAsset;
    private User user;

    @BeforeEach
    void setup() {
        securityContextService = mock(SecurityContextService.class);
        assignAssetRepository = mock(AssignAssetRepository.class);
        assignAssetMapper = mock(AssignAssetMapper.class);
        getAssignAssetServiceImpl = GetAssignAssetServiceImpl.builder()
                .assignAssetRepository(assignAssetRepository)
                .assignAssetMapper(assignAssetMapper)
                .securityContextService(securityContextService).build();
        assignAsset = mock(AssignAsset.class);
        user = mock(User.class);
    }

    @Test
    void detailAssignAsset_WhenAssignAssetEmpty_ShouldReturnException() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.findByIdAndUser(1L, user.getId())).thenReturn(Optional.empty());
        NotFoundException actualException = Assertions.assertThrows(NotFoundException.class,
                () -> getAssignAssetServiceImpl.getDetailAssignAssetOfUser(1L));

        Assertions.assertEquals("Cannot Found Assign Asset", actualException.getMessage());
    }

    @Test
    void detailAssignAsset_WhenAssignAssetNotEmpty_ShouldReturnException() {
        AssignAssetResponse expected = mock(AssignAssetResponse.class);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.findByIdAndUser(1L, user.getId())).thenReturn(Optional.of(assignAsset));
        when(assignAssetMapper.toAssignAssetResponse(assignAsset)).thenReturn(expected);

        AssignAssetResponse actual = getAssignAssetServiceImpl.getDetailAssignAssetOfUser(1L);

        assertThat(actual, is(expected));
    }

    @Test
    void findAssignAssetByUser_WhenFindNull_ShouldReturnException() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.findAllAssignAssetByUser(user.getId(), false, EAssignStatus.DECLINED, ""))
                .thenReturn(Collections.emptyList());

        NotFoundException actualException = Assertions.assertThrows(NotFoundException.class,
                () -> getAssignAssetServiceImpl.getAssignAssetOfUser());

        Assertions.assertEquals("User doesn't assign asset", actualException.getMessage());

    }

    @Test
    void findAssignAssetByUser_WhenFindNotNull_ShouldReturnData() {
        List<AssignAssetResponse> expected = mock(List.class);
        List<AssignAsset> assignAssets = mock(List.class);
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.findAllAssignAssetByUser(user.getId(), false, EAssignStatus.DECLINED,
                formatter.format(date)))
                .thenReturn(assignAssets);
        when(assignAssetMapper.toListAssignAssetResponses(assignAssets)).thenReturn(expected);

        List<AssignAssetResponse> actual = getAssignAssetServiceImpl.getAssignAssetOfUser();

        assertThat(actual, is(expected));
    }

    @Test
    void getAssignAssetDetails_WhenDataValid_ShouldReturnAssignAssetResponse() {
        Optional<AssignAsset> assignAssetOtp = Optional.of(assignAsset);
        AssignAssetResponse expected = mock(AssignAssetResponse.class);

        when(assignAssetRepository.findById(1L)).thenReturn(assignAssetOtp);
        when(assignAssetMapper.toAssignAssetResponse(assignAssetOtp.get())).thenReturn(expected);

        AssignAssetResponse actual = getAssignAssetServiceImpl.getAssignAssetDetails(1L);

        assertThat(actual, is(expected));
    }

    @Test
    void getAssignAssetDetails_WhenUserNotExist_ShouldThrowNotFoundException() {
        when(assignAssetRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> getAssignAssetServiceImpl.getAssignAssetDetails(1L));

        assertThat(actual.getMessage(), is("Cannot find assignment with id: 1"));
    }
}