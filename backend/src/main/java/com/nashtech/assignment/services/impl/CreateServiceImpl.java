package com.nashtech.assignment.services.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.nashtech.assignment.services.CreateService;
import com.nashtech.assignment.services.SecurityContextService;

import lombok.Builder;

@Service
@Builder
public class CreateServiceImpl implements CreateService {

  @Autowired
  private UserMapper userMapper;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private SecurityContextService securityContextService;
  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private CategoryMapper categoryMapper;
  @Autowired
  private AssetRepository assetRepository;
  @Autowired
  private AssetMapper assetMapper;

  public UserResponse createNewUser(CreateNewUserRequest createNewUserRequest) throws ParseException {
    SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
    Date dateOfBirth = formatterDate.parse(createNewUserRequest.getDateOfBirth());
    LocalDate birth = LocalDate.ofInstant(dateOfBirth.toInstant(), ZoneId.systemDefault());
    long age = LocalDate.from(birth).until(LocalDate.now(), ChronoUnit.YEARS);
    if (age < 18) {
      throw new BadRequestException("Age cannot below 18.");
    }

    Date joinedDate = formatterDate.parse(createNewUserRequest.getJoinedDate());
    LocalDate joinedDay = LocalDate.ofInstant(joinedDate.toInstant(), ZoneId.systemDefault());
    if (dateOfBirth.after(joinedDate)) {
      throw new BadRequestException("Joined date cannot be after birth date.");
    }

    DayOfWeek day = joinedDay.getDayOfWeek();
    if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
      throw new BadRequestException("Joined date cannot be Saturday or Sunday.");
    }

    User user = userMapper.toUser(createNewUserRequest);
    if (createNewUserRequest.getType() == EUserType.STAFF) {
      user.setLocation(securityContextService.getCurrentUser().getLocation());
    } else {
      if (createNewUserRequest.getLocation() == null || createNewUserRequest.getLocation().trim().equals(""))
        throw new BadRequestException("User type of ADMIN so location cannot be blank.");
      user.setLocation(createNewUserRequest.getLocation());
    }
    user = userRepository.save(user);

    NumberFormat formatter = new DecimalFormat("0000");
    String numberId = formatter.format(user.getId());
    StringBuilder staffCode = new StringBuilder("SD");
    StringBuilder userNameCode = new StringBuilder(createNewUserRequest.getFirstName());
    String[] listSingleWordLastName = createNewUserRequest.getLastName().split(" ");
    for (String word : listSingleWordLastName) {
      userNameCode.append(word.toLowerCase().charAt(0));
    }
    List<User> list = userRepository.findAllByUsernameMatchRegex(userNameCode.toString().toLowerCase() + "[0-9]?");
    if (list.size() > 0) {
      userNameCode.append(list.size());
    }

    user.setStaffCode(staffCode.append(numberId).toString());
    user.setUsername(userNameCode.toString().toLowerCase());
    user.setDateOfBirth(dateOfBirth);
    user.setJoinedDate(joinedDate);
    String birthPassword = createNewUserRequest.getDateOfBirth().replace("/", "");
    String password = user.getUsername() + "@" + birthPassword;
    user.setPassword(passwordEncoder.encode(password));

    userRepository.save(user);
    return userMapper.mapEntityToResponseDto(user);
  }

  @Override
  public CategoryResponse createNewCategory(CreateNewCategoryRequest createNewCategoryRequest) {
    if (!categoryRepository.findByName(createNewCategoryRequest.getCategoryName()).isEmpty()) {
      throw new BadRequestException("Category name is existed");
    }
    if (!categoryRepository.findByPrefixAssetCode(createNewCategoryRequest.getPrefixAssetCode()).isEmpty()){
      throw new BadRequestException("Prefix asset code is existed");
    }
    Category category = categoryMapper.mapCategoryRequestToEntity(createNewCategoryRequest);
    categoryRepository.save(category);
    return categoryMapper.toCategoryResponse(category);
  }

  @Override
  public AssetResponse createAssetResponse(CreateNewAssetRequest createNewAssetRequest) throws ParseException {
    Asset asset = assetMapper.mapAssetRequestToEntity(createNewAssetRequest);
    asset = assetRepository.save(asset);

    Optional<Category> categoryOpt = categoryRepository.findByName(createNewAssetRequest.getCategoryName());
    String prefixAssetCode = categoryOpt.get().getPrefixAssetCode();

    SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
    NumberFormat formatter = new DecimalFormat("000000");
    String assetId = formatter.format(asset.getId());
    StringBuilder assetCode = new StringBuilder(prefixAssetCode);

    Date installedDate = formatterDate.parse(createNewAssetRequest.getInstalledDate());

    asset.setAssetCode(assetCode.append(assetId).toString());
    asset.setName(createNewAssetRequest.getAssetName());
    asset.setSpecification(createNewAssetRequest.getSpecification());
    asset.setLocation(securityContextService.getCurrentUser().getLocation());
    asset.setStatus(createNewAssetRequest.getAssetStatus());
    asset.setInstalledDate(installedDate);

        assetRepository.save(asset);
        return assetMapper.toAssetResponse(asset);
    }
}
