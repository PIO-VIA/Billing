package com.example.account.modules.notification.repository;

import com.example.account.modules.notification.model.entity.Contact;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ContactConvertCallback implements AfterConvertCallback<Contact> {

    @Override
    public Publisher<Contact> onAfterConvert(Contact entity, SqlIdentifier table) {
        entity.setNewEntity(false);
        return Mono.just(entity);
    }
}
