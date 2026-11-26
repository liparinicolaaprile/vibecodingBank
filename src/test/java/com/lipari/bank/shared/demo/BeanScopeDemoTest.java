package com.lipari.bank.shared.demo;

import com.lipari.bank.shared.demo.BeanScopeDemo.PrototypeScopedBean;
import com.lipari.bank.shared.demo.BeanScopeDemo.SingletonScopedBean;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Dimostra la differenza Singleton vs Prototype iniettando ciascun bean due volte.
 * <p>
 * {@code @TestConstructor(ALL)} permette la constructor injection anche nel test,
 * senza {@code @Autowired}. Ogni parametro del costruttore e' un injection point
 * distinto: per un prototype Spring crea quindi un'istanza per parametro.
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
@TestConstructor(autowireMode = AutowireMode.ALL)
class BeanScopeDemoTest {

    private final SingletonScopedBean singletonA;
    private final SingletonScopedBean singletonB;
    private final PrototypeScopedBean prototypeA;
    private final PrototypeScopedBean prototypeB;
    private final ApplicationContext context;

    BeanScopeDemoTest(SingletonScopedBean singletonA,
                      SingletonScopedBean singletonB,
                      PrototypeScopedBean prototypeA,
                      PrototypeScopedBean prototypeB,
                      ApplicationContext context) {
        this.singletonA = singletonA;
        this.singletonB = singletonB;
        this.prototypeA = prototypeA;
        this.prototypeB = prototypeB;
        this.context = context;
    }

    @Test
    void singletonInjectedTwiceIsTheSameReference() {
        log.info("SINGLETON  A hash={}  B hash={}  -> stesso oggetto? {}",
                Integer.toHexString(singletonA.hashCode()),
                Integer.toHexString(singletonB.hashCode()),
                singletonA == singletonB);

        assertThat(singletonA).isSameAs(singletonB);               // ==
        assertThat(singletonA.hashCode()).isEqualTo(singletonB.hashCode());
        assertThat(singletonA.instanceNumber()).isEqualTo(singletonB.instanceNumber());
    }

    @Test
    void prototypeInjectedTwiceIsADifferentReference() {
        log.info("PROTOTYPE  A hash={}  B hash={}  -> stesso oggetto? {}",
                Integer.toHexString(prototypeA.hashCode()),
                Integer.toHexString(prototypeB.hashCode()),
                prototypeA == prototypeB);

        assertThat(prototypeA).isNotSameAs(prototypeB);            // !=
        assertThat(prototypeA.instanceNumber()).isNotEqualTo(prototypeB.instanceNumber());
    }

    @Test
    void counterShowsHowManyInstancesEachScopeCreates() {
        // Conteggi relativi: il contesto di test e' condiviso (cache), quindi
        // si misura la variazione invece del valore assoluto.
        int singletonsBefore = SingletonScopedBean.instancesCreated();
        int prototypesBefore = PrototypeScopedBean.instancesCreated();

        for (int i = 0; i < 3; i++) {
            context.getBean(SingletonScopedBean.class);
            context.getBean(PrototypeScopedBean.class);
        }

        int newSingletons = SingletonScopedBean.instancesCreated() - singletonsBefore;
        int newPrototypes = PrototypeScopedBean.instancesCreated() - prototypesBefore;
        log.info("Dopo 3 getBean(): nuovi SINGLETON = {}, nuovi PROTOTYPE = {}", newSingletons, newPrototypes);

        assertThat(newSingletons).isZero();     // sempre la stessa istanza, gia' creata all'avvio
        assertThat(newPrototypes).isEqualTo(3); // una nuova istanza per ogni richiesta
        assertThat(SingletonScopedBean.instancesCreated()).isEqualTo(1);
    }
}
