package com.example.account.modules.core.util;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class LineIdSupport {

    private LineIdSupport() {
    }

    public static <T> void assignMissingIds(
            List<T> items,
            Function<T, UUID> getter,
            BiConsumer<T, UUID> setter) {
        if (items == null) {
            return;
        }
        for (T item : items) {
            if (item != null && getter.apply(item) == null) {
                setter.accept(item, UUID.randomUUID());
            }
        }
    }
}
