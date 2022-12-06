package com.nashtech.assignment.services.get.impl;

import static org.mockito.Mockito.mock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;

public class FindAssignAssetServiceImplTest {
    private SecurityContextService securityContextService;
    private AssignAssetRepository assignAssetRepository;
    private AssignAssetMapper assignAssetMapper;
    private AssignAsset assignAsset;
    private FindAssignAssetServiceImpl findAssignAssetImpl;

    @BeforeEach
    void setUpTest() {
        securityContextService = mock(SecurityContextService.class);
        assignAssetRepository = mock(AssignAssetRepository.class);
        assignAssetMapper = mock(AssignAssetMapper.class);
        assignAsset = mock(AssignAsset.class);
        findAssignAssetImpl = new FindAssignAssetServiceImpl(securityContextService, assignAssetRepository,
                assignAssetMapper);
    }

    @Test
    void detailAssignAsset_WhenAssignAssetEmpty_ShouldReturnException() {
        User user = mock(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.findByIdAndUser(1L, user.getId())).thenReturn(Optional.empty());
        NotFoundException actualException = Assertions.assertThrows(NotFoundException.class,
                () -> findAssignAssetImpl.detailAssignAsset(1L));

        Assertions.assertEquals("Cannot Found Assign Asset", actualException.getMessage());

    }

    @Test
    void detailAssignAsset_WhenAssignAssetNotEmpty_ShouldReturnException() {
        User user = mock(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        AssignAssetResponse expected = mock(AssignAssetResponse.class);

        when(assignAssetRepository.findByIdAndUser(1L, user.getId())).thenReturn(Optional.of(assignAsset));
        when(assignAssetMapper.toAssignAssetResponse(assignAsset)).thenReturn(expected);

        AssignAssetResponse actual = findAssignAssetImpl.detailAssignAsset(1L);

        assertThat(actual, is(expected));
    }

    @Test
    void findAssignAssetByUser_WhenFindNull_ShouldReturnException() {
        User user = mock(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.findAllAssignAssetByUser(user.getId(), false, EAssignStatus.DECLINED))
                .thenReturn(Collections.emptyList());

        NotFoundException actualException = Assertions.assertThrows(NotFoundException.class,
                () -> findAssignAssetImpl.findAssignAssetByUser());

        Assertions.assertEquals("User doesn't assign asset", actualException.getMessage());

    }

    @Test
    void findAssignAssetByUser_WhenFindNotNull_ShouldReturnData() {
        List<AssignAssetResponse> expected = mock(List.class);
        List<AssignAsset> assignAssets = mock(List.class);
        User user = mock(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.findAllAssignAssetByUser(user.getId(), false, EAssignStatus.DECLINED)).thenReturn(assignAssets);
        when(assignAssetMapper.mapListEntityToDto(assignAssets)).thenReturn(expected);

        List<AssignAssetResponse> actual = findAssignAssetImpl.findAssignAssetByUser();

        assertThat(actual, is(expected));
    }
}
