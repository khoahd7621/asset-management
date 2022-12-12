package com.nashtech.assignment.services.create.impl;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.ReturnAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.ReturnAssetRepository;
import com.nashtech.assignment.dto.response.returned.ReturnAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.ReturnAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateReturnAssetServiceImplTest {

    private CreateReturnAssetServiceImpl createReturnAssetServiceImp;
    private ReturnAssetRepository returnAssetRepository;
    private AssignAssetRepository assignAssetRepository;
    private ReturnAssetMapper returnAssetMapper;
    private AssignAsset assignAsset;
    private ReturnAsset returnAsset;
    private SecurityContextService securityContextService;

    @BeforeEach
    void setUp() {
        assignAsset = mock(AssignAsset.class);
        returnAsset = mock(ReturnAsset.class);
        securityContextService = mock(SecurityContextService.class);
        returnAssetRepository = mock(ReturnAssetRepository.class);
        assignAssetRepository = mock(AssignAssetRepository.class);
        returnAssetMapper = mock(ReturnAssetMapper.class);
        createReturnAssetServiceImp = CreateReturnAssetServiceImpl.builder()
                .returnAssetRepository(returnAssetRepository)
                .assignAssetRepository(assignAssetRepository)
                .returnAssetMapper(returnAssetMapper)
                .securityContextService(securityContextService).build();
    }

    @Test
    void createReturnAsset_WhenAssignNotFound_ShouldReturnNotFoundException() {
        when(assignAssetRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            createReturnAssetServiceImp.createReturnAsset(1L);

        });
        assertThat(actual.getMessage(), is("Assignment not found"));

    }

    @Test
    void createReturnAsset_WhenAssignNotFound_ShouldReturnForbiddenException() {
        User userAssignedTo = User.builder().id(1L).build();
        User userCurrent = User.builder().id(2L).type(EUserType.STAFF).build();

        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(securityContextService.getCurrentUser()).thenReturn(userCurrent);
        when(assignAsset.getUserAssignedTo()).thenReturn(userAssignedTo);

        ForbiddenException actual = assertThrows(ForbiddenException.class, () -> {
            createReturnAssetServiceImp.createReturnAsset(1L);
        });

        assertThat(actual.getMessage(),
                is("Current user is not match to this assignment or current user is not admin."));

    }

    @Test
    void createReturnAsset_WhenAssignNotFound_ShouldReturnBadRequestexception() {
        User userAssignedTo = User.builder().id(1L).build();
        User userCurrent = User.builder().id(2L).type(EUserType.ADMIN).build();

        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(securityContextService.getCurrentUser()).thenReturn(userCurrent);
        when(assignAsset.getUserAssignedTo()).thenReturn(userAssignedTo);
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.WAITING_FOR_ACCEPTANCE);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            createReturnAssetServiceImp.createReturnAsset(1L);

        });
        assertThat(actual.getMessage(), is("Assignment is not accepted or Assignment already exist in return list"));

    }

    @Test
    void createReturnAsset_WhenDataValid_ShouldReturnData() throws Exception {
        ReturnAssetResponse expected = mock(ReturnAssetResponse.class);
        ArgumentCaptor<ReturnAsset> returnAssetCaptor = ArgumentCaptor.forClass(ReturnAsset.class);
        User userAssignedTo = User.builder().id(1L).build();
        User userCurrent = User.builder().id(1L).type(EUserType.STAFF).build();

        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(securityContextService.getCurrentUser()).thenReturn(userCurrent);
        when(assignAsset.getUserAssignedTo()).thenReturn(userAssignedTo);
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.ACCEPTED);
        when(returnAssetRepository.findByAssignAssetId(1L)).thenReturn(Optional.empty());
        when(returnAssetRepository.save(returnAssetCaptor.capture())).thenReturn(returnAsset);
        when(returnAssetMapper.toReturnAssetResponse(returnAsset)).thenReturn(expected);

        ReturnAssetResponse actual = createReturnAssetServiceImp.createReturnAsset(1L);
        assertThat(actual, is(expected));
    }

    @Test
    void createReturnAsset_WhenUserIsAdmin_ShouldReturnData() throws Exception {
        ReturnAssetResponse expected = mock(ReturnAssetResponse.class);
        ArgumentCaptor<ReturnAsset> returnAssetCaptor = ArgumentCaptor.forClass(ReturnAsset.class);
        User userAssignedTo = User.builder().id(1L).build();
        User userCurrent = User.builder().id(1L).type(EUserType.ADMIN).build();

        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(securityContextService.getCurrentUser()).thenReturn(userCurrent);
        when(assignAsset.getUserAssignedTo()).thenReturn(userAssignedTo);
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.ACCEPTED);
        when(returnAssetRepository.findByAssignAssetId(1L)).thenReturn(Optional.empty());
        when(returnAssetRepository.save(returnAssetCaptor.capture())).thenReturn(returnAsset);
        when(returnAssetMapper.toReturnAssetResponse(returnAsset)).thenReturn(expected);

        ReturnAssetResponse actual = createReturnAssetServiceImp.createReturnAsset(1L);
        assertThat(actual, is(expected));
    }
}
