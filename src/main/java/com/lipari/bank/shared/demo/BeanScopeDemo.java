package com.lipari.bank.shared.demo;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mette a confronto i due scope principali di Spring.
 * <ul>
 *   <li>{@link SingletonScopedBean}: un'unica istanza per ApplicationContext,
 *       restituita a ogni richiesta (stesso riferimento, stesso hashCode).</li>
 *   <li>{@link PrototypeScopedBean}: una nuova istanza a ogni richiesta
 *       (injection point o {@code getBean}), quindi riferimenti e hashCode diversi.</li>
 * </ul>
 * Ogni bean ha un proprio contatore statico: ogni volta che il costruttore
 * gira, il contatore sale. Leggerlo prima e dopo una richiesta mostra quante
 * istanze sono state davvero create.
 */
public final class BeanScopeDemo {

    private BeanScopeDemo() {
    }

    @Slf4j
    @Component
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) // default, esplicitato per chiarezza
    public static class SingletonScopedBean {

        private static final AtomicInteger INSTANCES_CREATED = new AtomicInteger();

        private final int instanceNumber;

        public SingletonScopedBean() {
            this.instanceNumber = INSTANCES_CREATED.incrementAndGet();
        }

        @PostConstruct
        void logCreation() {
            log.info(">>> [SINGLETON]  istanza #{} creata | hashCode = {} | istanze totali = {}",
                    instanceNumber, Integer.toHexString(hashCode()), INSTANCES_CREATED.get());
        }

        public static int instancesCreated() {
            return INSTANCES_CREATED.get();
        }

        public int instanceNumber() {
            return instanceNumber;
        }
    }

    @Slf4j
    @Component
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public static class PrototypeScopedBean {

        private static final AtomicInteger INSTANCES_CREATED = new AtomicInteger();

        private final int instanceNumber;

        public PrototypeScopedBean() {
            this.instanceNumber = INSTANCES_CREATED.incrementAndGet();
        }

        @PostConstruct
        void logCreation() {
            log.info(">>> [PROTOTYPE]  istanza #{} creata | hashCode = {} | istanze totali = {}",
                    instanceNumber, Integer.toHexString(hashCode()), INSTANCES_CREATED.get());
        }

        public static int instancesCreated() {
            return INSTANCES_CREATED.get();
        }

        public int instanceNumber() {
            return instanceNumber;
        }
    }
}
