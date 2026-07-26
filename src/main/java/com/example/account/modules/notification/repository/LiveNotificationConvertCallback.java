package com.example.account.modules.notification.repository;

import com.example.account.modules.notification.model.entity.LiveNotification;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LiveNotificationConvertCallback implements AfterConvertCallback<LiveNotification> {

    @Override
    public Publisher<LiveNotification> onAfterConvert(LiveNotification entity, SqlIdentifier table) {
        entity.setNewEntity(false);
        return Mono.just(entity);
    }
}
