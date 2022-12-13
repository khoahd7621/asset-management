package com.nashtech.assignment.utils;

import lombok.Builder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
@Builder
public class CompareDateUtil {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    public boolean isEquals(Date dateNeedToCompare, Date dateUsedToCompare) {
        LocalDate localDateNeedToCompare = LocalDate.ofInstant(dateNeedToCompare.toInstant(), ZONE_ID);
        LocalDate localDateUsedToCompare = LocalDate.ofInstant(dateUsedToCompare.toInstant(), ZONE_ID);
        return localDateNeedToCompare.isEqual(localDateUsedToCompare);
    }

    public boolean isBefore(Date dateNeedToCompare, Date dateUsedToCompare) {
        LocalDate localDateNeedToCompare = LocalDate.ofInstant(dateNeedToCompare.toInstant(), ZONE_ID);
        LocalDate localDateUsedToCompare = LocalDate.ofInstant(dateUsedToCompare.toInstant(), ZONE_ID);
        return localDateNeedToCompare.isBefore(localDateUsedToCompare);
    }

    public boolean isAfter(Date dateNeedToCompare, Date dateUsedToCompare) {
        LocalDate localDateNeedToCompare = LocalDate.ofInstant(dateNeedToCompare.toInstant(), ZONE_ID);
        LocalDate localDateUsedToCompare = LocalDate.ofInstant(dateUsedToCompare.toInstant(), ZONE_ID);
        return localDateNeedToCompare.isAfter(localDateUsedToCompare);
    }
}
