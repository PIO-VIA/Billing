package com.example.account.modules.core.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class DocumentNumberGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    private DocumentNumberGenerator() {
    }

    public static String generate(String prefix) {
        String datePart = LocalDate.now().format(DATE_FORMAT);
        String uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + datePart + "-" + uniquePart;
    }
}
