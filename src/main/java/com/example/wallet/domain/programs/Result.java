package com.example.wallet.domain.programs;

import com.example.wallet.domain.DomainEventHandler;
import com.example.wallet.domain.entities.event.*;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class Result<T> {

    public final T value;

    public final List<DomainEvent> events;

    private Result(T value, @NonNull List<DomainEvent> events) {
        this.value = value;
        this.events = events;
    }

    public Result(T value) {
        this(value, List.of());
    }

    public static Result<Void> empty() {
        return new Result<>(null);
    }


    static class Builder<T> {
        private Function<T, T> transition;

        private final DomainEventHandler<Function<T, T>> transistor;

        private final List<DomainEvent> events;

        public Builder(List<DomainEvent> events, DomainEventHandler<Function<T, T>> transistor) {
            this.transition = Function.identity();
            this.events = new ArrayList<>(events);
            this.transistor = transistor;
        }

        public Builder(DomainEventHandler<Function<T, T>> transistor) {
            this(List.of(), transistor);
        }

        public Result<T> build(T state) {
            return new Result<>(transition.apply(state), Collections.unmodifiableList(events));
        }

        public Builder<T> addEvent(TransactionConfirmed event) {
            this.transition = transition.andThen(transistor.handle(event));
            events.add(event);
            return this;
        }

        public Builder<T> addEvent(TransactionCommitted event) {
            this.transition = transition.andThen(transistor.handle(event));
            events.add(event);
            return this;
        }

        public Builder<T> addEvent(TransactionRollback event) {
            this.transition = transition.andThen(transistor.handle(event));
            events.add(event);
            return this;
        }

        public Builder<T> addEvent(TransactionStarted event) {
            this.transition = transition.andThen(transistor.handle(event));
            events.add(event);
            return this;
        }

        public Builder<T> addEvent(Deposited event) {
            this.transition = transition.andThen(transistor.handle(event));
            events.add(event);
            return this;
        }

        public Builder<T> addEvent(Withdrawn event) {
            this.transition = transition.andThen(transistor.handle(event));
            events.add(event);
            return this;
        }

        public Builder<T> addEvent(WalletCreated event) {
            this.transition = transition.andThen(transistor.handle(event));
            events.add(event);
            return this;
        }
    }

    public static <T> Builder<T> builder(DomainEventHandler<Function<T, T>> transistor) {
        return new Builder<>(transistor);
    }
}
