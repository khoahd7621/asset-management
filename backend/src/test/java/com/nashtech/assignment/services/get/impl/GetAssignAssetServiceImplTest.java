package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @BeforeEach
    void setup() {
        assignAssetRepository = mock(AssignAssetRepository.class);
        assignAssetMapper = mock(AssignAssetMapper.class);
        getAssignAssetServiceImpl = GetAssignAssetServiceImpl.builder()
                .assignAssetRepository(assignAssetRepository)
                .assignAssetMapper(assignAssetMapper).build();
    }

    @Test
    void getAssignAssetDetails_WhenDataValid_ShouldReturnAssignAssetResponse() {
        AssignAsset assignAsset = mock(AssignAsset.class);
        Optional<AssignAsset> assignAssetOtp = Optional.of(assignAsset);
        AssignAssetResponse expected = mock(AssignAssetResponse.class);

        when(assignAssetRepository.findById(1L)).thenReturn(assignAssetOtp);
        when(assignAssetMapper.toAssignAssetResponse(assignAssetOtp.get())).thenReturn(expected);

        AssignAssetResponse actual = getAssignAssetServiceImpl.getAssignAssetDetails(1L);

        assertThat(actual, is(expected));
    }

    @Test
    void getAssignAssetDetails_WhenUserNotExist_ShouldThrowNotFoundException() {
        AssignAsset assignAsset = mock(AssignAsset.class);

        when(assignAssetRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> getAssignAssetServiceImpl.getAssignAssetDetails(1L));

        assertThat(actual.getMessage(), is("Cannot find assignment with id: 1"));
    }
}