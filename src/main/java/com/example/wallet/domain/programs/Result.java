package com.example.wallet.domain.programs;

import com.example.wallet.domain.entities.event.DomainEvent;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Result<T> {

    public final T value;

    public final List<DomainEvent> events;

    private Result(@NonNull T value, @NonNull List<DomainEvent> events) {
        this.value = value;
        this.events = events;
    }

    public Result(@NonNull T value) {
        this(value, List.of());
    }

    public <U> Builder<U> toBuilder() {
        return new Builder<>(events);
    }

    static class Builder<T> {
        private final List<DomainEvent> events;

        public Builder(List<DomainEvent> events) {
            this.events = new ArrayList<>(events);
        }

        public Builder() {
            this(List.of());
        }

        public Builder<T> addEvent(DomainEvent event) {
            this.events.add(event);
            return this;
        }

        public Builder<T> addEvents(List<DomainEvent> events) {
            this.events.addAll(events);
            return this;
        }

        public Result<T> build(T result) {
            return new Result<>(result, Collections.unmodifiableList(events));
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
}
