package com.bramix.perfomance.tracker.utility;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class JqlUtility {
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public String createSearchByUpdatedDateDateQuery(LocalDateTime startDate, LocalDateTime endDate) {
        return String.format("updatedDate >= %s and updatedDate <= %s", DATE_FORMATTER.format(startDate), DATE_FORMATTER.format(endDate));
    }

}
