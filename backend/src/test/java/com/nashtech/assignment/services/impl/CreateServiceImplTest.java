package com.nashtech.assignment.services.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EGender;
import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.CategoryRepository;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.asset.CreateNewAssetRequest;
import com.nashtech.assignment.dto.request.category.CreateNewCategoryRequest;
import com.nashtech.assignment.dto.request.user.CreateNewUserRequest;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.mappers.CategoryMapper;
import com.nashtech.assignment.mappers.UserMapper;
import com.nashtech.assignment.services.SecurityContextService;

class CreateServiceImplTest {

  private CreateServiceImpl createService;
  private UserRepository userRepository;
  private CategoryRepository categoryRepository;
  private AssetRepository assetRepository;
  private AssetMapper assetMapper;
  private UserMapper userMapper;
  private CategoryMapper categoryMapper;
  private PasswordEncoder passwordEncoder;
  private SecurityContextService securityContextService;
  private User user;
  private Category category;
  private Asset asset;

  @BeforeEach
  void beforeEach() {
    userRepository = mock(UserRepository.class);
    categoryRepository = mock(CategoryRepository.class);
    assetRepository = mock(AssetRepository.class);
    userMapper = mock(UserMapper.class);
    categoryMapper = mock(CategoryMapper.class);
    assetMapper = mock(AssetMapper.class);
    passwordEncoder = mock(PasswordEncoder.class);
    securityContextService = mock(SecurityContextService.class);
    createService = CreateServiceImpl.builder()
        .userRepository(userRepository)
        .userMapper(userMapper)
        .passwordEncoder(passwordEncoder)
        .securityContextService(securityContextService)
        .categoryRepository(categoryRepository)
        .categoryMapper(categoryMapper)
        .assetRepository(assetRepository)
        .assetMapper(assetMapper)
        .build();
    user = mock(User.class);
    asset = mock(Asset.class);
    category = mock(Category.class);
  }

  @Test
  void createNewUser_WhenAgeLessThan18_ShouldThrowBadRequestException() {
    CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
        .dateOfBirth("20/12/2004")
        .build();

    BadRequestException actual = assertThrows(BadRequestException.class, () -> {
      createService.createNewUser(createNewUserRequest);
    });

    assertThat(actual.getMessage(), is("Age cannot below 18."));
  }

  @Test
  void createNewUser_WhenBirthAfterJoinDate_ShouldThrowBadRequestException() {
    CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
        .dateOfBirth("21/12/2001")
        .joinedDate("20/12/2001")
        .build();

    BadRequestException actual = assertThrows(BadRequestException.class, () -> {
      createService.createNewUser(createNewUserRequest);
    });

    assertThat(actual.getMessage(), is("Joined date cannot be after birth date."));
  }

  @Test
  void createNewUser_WhenJoinDateIsSunday_ShouldThrowBadRequestException() {
    CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
        .dateOfBirth("21/12/2001")
        .joinedDate("20/11/2022")
        .build();

    BadRequestException actual = assertThrows(BadRequestException.class, () -> {
      createService.createNewUser(createNewUserRequest);
    });

    assertThat(actual.getMessage(), is("Joined date cannot be Saturday or Sunday."));
  }

  @Test
  void createNewUser_WhenJoinDateIsSaturday_ShouldThrowBadRequestException() {
    CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
        .dateOfBirth("21/12/2001")
        .joinedDate("19/11/2022")
        .build();

    BadRequestException actual = assertThrows(BadRequestException.class, () -> {
      createService.createNewUser(createNewUserRequest);
    });

    assertThat(actual.getMessage(), is("Joined date cannot be Saturday or Sunday."));
  }

  @Test
  void createNewUser_WhenCreateAdminButLocationIsNull_ShouldThrowBadRequestException() {
    CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
        .dateOfBirth("21/12/2001")
        .joinedDate("17/11/2022")
        .type(EUserType.ADMIN)
        .location(null)
        .build();

    when(userMapper.toUser(createNewUserRequest)).thenReturn(user);

    BadRequestException actual = assertThrows(BadRequestException.class, () -> {
      createService.createNewUser(createNewUserRequest);
    });

    assertThat(actual.getMessage(), is("User type of ADMIN so location cannot be blank."));
  }

  @Test
  void createNewUser_WhenDataValid_ShouldReturnData() throws ParseException {
    CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
        .firstName("hau")
        .lastName("doan")
        .dateOfBirth("21/12/2001")
        .joinedDate("17/11/2022")
        .gender(EGender.MALE)
        .type(EUserType.ADMIN)
        .location("hehe")
        .build();
    ArgumentCaptor<String> staffCodeCaptor = ArgumentCaptor.forClass(String.class);
    SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
    Date dateOfBirth = formatterDate.parse(createNewUserRequest.getDateOfBirth());
    Date joinedDate = formatterDate.parse(createNewUserRequest.getJoinedDate());
    UserResponse expected = mock(UserResponse.class);

    when(userRepository.save(user)).thenReturn(user);
    when(user.getFirstName()).thenReturn(createNewUserRequest.getFirstName());
    when(userMapper.toUser(createNewUserRequest)).thenReturn(user);
    when(user.getUsername()).thenReturn("haud");
    when(userMapper.mapEntityToResponseDto(user)).thenReturn(expected);

    UserResponse actual = createService.createNewUser(createNewUserRequest);

    verify(user).setStaffCode(staffCodeCaptor.capture());
    verify(user).setUsername("haud");
    verify(user).setDateOfBirth(dateOfBirth);
    verify(user).setJoinedDate(joinedDate);
    verify(user).setPassword(passwordEncoder.encode("haud@21122001"));
    assertThat(staffCodeCaptor.getValue(), is("SD0000"));
    assertThat(actual, is(expected));
  }

  @Test
  void testCreateNewCategory_WhenCategoryNameExisted_ShouldReturnException() {
    CreateNewCategoryRequest createNewCategoryRequest = CreateNewCategoryRequest.builder()
        .categoryName("categoryName")
        .prefixAssetCode("CN")
        .build();

    when(categoryRepository.findByName(createNewCategoryRequest.getCategoryName())).thenReturn(Optional.of(category));

    BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
        () -> createService.createNewCategory(createNewCategoryRequest));

    Assertions.assertEquals("Category name is existed", actualException.getMessage());
  }

  @Test
  void testCreateNewCategory_WhenAssetCodeExisted_ShouldReturnException() {
    CreateNewCategoryRequest createNewCategoryRequest = CreateNewCategoryRequest.builder()
        .categoryName("categoryName")
        .prefixAssetCode("CN")
        .build();

    when(categoryRepository.findByName(createNewCategoryRequest.getCategoryName())).thenReturn(Optional.empty());
    when(categoryRepository.findByPrefixAssetCode(createNewCategoryRequest.getPrefixAssetCode()))
        .thenReturn(Optional.of(category));

    BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
        () -> createService.createNewCategory(createNewCategoryRequest));

    Assertions.assertEquals("Prefix asset code is existed", actualException.getMessage());
  }

  @Test
  void testCreateNewCategory_WhenDataValid_ShouldReturnData() throws BadRequestException {
    CreateNewCategoryRequest createNewCategoryRequest = CreateNewCategoryRequest.builder()
        .categoryName("categoryName")
        .prefixAssetCode("CN")
        .build();

    CategoryResponse expected = mock(CategoryResponse.class);

    when(categoryRepository.findByName(createNewCategoryRequest.getCategoryName())).thenReturn(Optional.empty());
    when(categoryRepository.findByPrefixAssetCode(createNewCategoryRequest.getPrefixAssetCode()))
        .thenReturn(Optional.empty());
    when(categoryMapper.mapCategoryRequestToEntity(createNewCategoryRequest)).thenReturn(category);
    when(categoryRepository.save(category)).thenReturn(category);
    when(categoryMapper.toCategoryResponse(category)).thenReturn(expected);

    CategoryResponse actual = createService.createNewCategory(createNewCategoryRequest);

    assertThat(actual, is(expected));
  }

  @Test
  void testCreateAssetResponse_WhenDataValid_ShouldReturnData() throws ParseException {
    String prefixAssetCode = "CN";
    User currentUser = User.builder().staffCode("currentUser").build();

    CreateNewAssetRequest createNewAssetRequest = CreateNewAssetRequest.builder()
        .assetName("assetName")
        .categoryName("categoryName")
        .specification("specification")
        .installedDate("01/01/2022")
        .assetStatus(EAssetStatus.AVAILABLE)
        .build();

    AssetResponse expected = mock(AssetResponse.class);

    when(assetMapper.mapAssetRequestToEntity(createNewAssetRequest)).thenReturn(asset);
    when(assetRepository.save(asset)).thenReturn(asset);

    when(categoryRepository.findByName(createNewAssetRequest.getCategoryName())).thenReturn(Optional.of(category));
    when(category.getPrefixAssetCode()).thenReturn(prefixAssetCode);
    when(assetMapper.toAssetResponse(asset)).thenReturn(expected);
    when(securityContextService.getCurrentUser()).thenReturn(currentUser);

    SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
    NumberFormat formatter = new DecimalFormat("100000");
    String assetId = formatter.format(asset.getId());
    StringBuilder assetCode = new StringBuilder(prefixAssetCode);
    Date installedDate = formatterDate.parse(createNewAssetRequest.getInstalledDate());

    AssetResponse actual = createService.createAssetResponse(createNewAssetRequest);

    verify(asset).setAssetCode(assetCode.append(assetId).toString());
    verify(asset).setName(createNewAssetRequest.getAssetName());
    verify(asset).setSpecification(createNewAssetRequest.getSpecification());
    verify(asset).setLocation(securityContextService.getCurrentUser().getLocation());
    verify(asset).setInstalledDate(installedDate);
    verify(asset).setStatus(EAssetStatus.AVAILABLE);

    assertThat(actual, is(expected));
  }
}