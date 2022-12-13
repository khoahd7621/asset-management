package com.nashtech.assignment;

import com.nashtech.assignment.controllers.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AssignmentApplicationTests {

    @Autowired
    private AssetController assetController;
    @Autowired
    private AssignAssetController assignAssetController;
    @Autowired
    private AuthController authController;
    @Autowired
    private CategoryController categoryController;
    @Autowired
    private ReportController reportController;
    @Autowired
    private ReturnAssetController returnAssetController;
    @Autowired
    private UserController userController;

    @Test
    void contextLoads() {
        Assertions.assertThat(assetController).isNotNull();
        Assertions.assertThat(assignAssetController).isNotNull();
        Assertions.assertThat(authController).isNotNull();
        Assertions.assertThat(categoryController).isNotNull();
        Assertions.assertThat(reportController).isNotNull();
        Assertions.assertThat(returnAssetController).isNotNull();
        Assertions.assertThat(userController).isNotNull();
    }

}
