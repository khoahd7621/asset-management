package com.nashtech.assignment.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class CompareDateUtilTest {

    private CompareDateUtil compareDateUtil;

    private Date today;
    private Date yesterday;
    private Date tomorrow;

    @BeforeEach
    void setup() {
        compareDateUtil = CompareDateUtil.builder().build();
        today = new Date();
        yesterday = new Date(today.getTime() - (1000 * 60 * 60 * 24));
        tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));
    }

    @Test
    void isEquals_WhenCompareTheSameDate_ShouldReturnTrue() {
        boolean actual = compareDateUtil.isEquals(today, today);
        assertThat(actual, is(true));
    }

    @Test
    void isEquals_WhenCompareDifferentDate_ShouldReturnFalse() {
        boolean actual = compareDateUtil.isEquals(yesterday, today);
        assertThat(actual, is(false));
    }

    @Test
    void isBefore_WhenComparePastDateWithToday_ShouldReturnTrue() {
        boolean actual = compareDateUtil.isBefore(yesterday, today);
        assertThat(actual, is(true));
    }

    @Test
    void isBefore_WhenCompareToDayWithFutureDate_ShouldReturnTrue() {
        boolean actual = compareDateUtil.isBefore(today, tomorrow);
        assertThat(actual, is(true));
    }

    @Test
    void isBefore_WhenCompareTodayWithPastDate_ShouldReturnFalse() {
        boolean actual = compareDateUtil.isBefore(today, yesterday);
        assertThat(actual, is(false));
    }

    @Test
    void isBefore_WhenCompareFutureDateWithPastDate_ShouldReturnFalse() {
        boolean actual = compareDateUtil.isBefore(tomorrow, yesterday);
        assertThat(actual, is(false));
    }

    @Test
    void isBefore_WhenCompareFutureDateWithToDay_ShouldReturnFalse() {
        boolean actual = compareDateUtil.isBefore(tomorrow, today);
        assertThat(actual, is(false));
    }

    @Test
    void isBefore_WhenCompareTheSameDate_ShouldReturnFalse() {
        boolean actual = compareDateUtil.isBefore(today, today);
        assertThat(actual, is(false));
    }

    @Test
    void isAfter_WhenCompareFutureDateWithToday_ShouldReturnTrue() {
        boolean actual = compareDateUtil.isAfter(tomorrow, today);
        assertThat(actual, is(true));
    }

    @Test
    void isAfter_WhenCompareFutureDateWithYesterday_ShouldReturnTrue() {
        boolean actual = compareDateUtil.isAfter(tomorrow, yesterday);
        assertThat(actual, is(true));
    }

    @Test
    void isAfter_WhenCompareTodayWithFutureDate_ShouldReturnFalse() {
        boolean actual = compareDateUtil.isAfter(today, tomorrow);
        assertThat(actual, is(false));
    }

    @Test
    void isAfter_WhenCompareYesterdayWithFutureDate_ShouldReturnFalse() {
        boolean actual = compareDateUtil.isAfter(yesterday, tomorrow);
        assertThat(actual, is(false));
    }

    @Test
    void isAfter_WhenCompareTheSameDate_ShouldReturnFalse() {
        boolean actual = compareDateUtil.isAfter(today, today);
        assertThat(actual, is(false));
    }
}