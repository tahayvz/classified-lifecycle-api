package com.marketplace.classifieds.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@DisplayName("Hexagonal architecture boundaries")
class HexagonalArchitectureTest {

    private static final String BASE = "com.marketplace.classifieds";

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE);
    }

    @Test
    void domainMustNotDependOnSpring() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".domain..")
                .should().dependOnClassesThat().resideInAnyPackage("org.springframework..")
                .because("business rules must stay runnable without a Spring context");

        rule.check(classes);
    }

    @Test
    void domainMustNotDependOnAdapters() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".domain..")
                .should().dependOnClassesThat().resideInAPackage(BASE + ".adapter..")
                .because("the domain must not know how it is delivered or persisted");

        rule.check(classes);
    }

    @Test
    void domainMustNotDependOnApplicationLayer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".domain..")
                .should().dependOnClassesThat().resideInAPackage(BASE + ".application..")
                .because("dependencies point inward, never from the core to its orchestration");

        rule.check(classes);
    }

    @Test
    void inboundAdaptersMustNotDependOnOutboundAdapters() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".adapter.in..")
                .should().dependOnClassesThat().resideInAPackage(BASE + ".adapter.out..")
                .because("adapters talk through ports, never directly to each other");

        rule.check(classes);
    }

    @Test
    void applicationMustNotDependOnOutboundAdapters() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".application..")
                .should().dependOnClassesThat().resideInAPackage(BASE + ".adapter.out..")
                .because("use cases depend on outbound ports, not on their implementations");

        rule.check(classes);
    }

    @Test
    void portsMustBeInterfaces() {
        ArchRule rule = classes()
                .that().resideInAPackage(BASE + ".domain.port..")
                .should().beInterfaces()
                .because("a port is a contract, not an implementation");

        rule.check(classes);
    }

    @Test
    void persistenceAdaptersMustImplementOutboundPorts() {
        ArchRule rule = classes()
                .that().resideInAPackage(BASE + ".adapter.out.persistence")
                .and().haveSimpleNameEndingWith("PersistenceAdapter")
                .should().implement(
                        com.tngtech.archunit.base.DescribedPredicate.describe(
                                "an outbound port",
                                javaClass -> javaClass.getPackageName()
                                        .startsWith(BASE + ".domain.port.out")))
                .because("persistence adapters exist to satisfy a domain port");

        rule.check(classes);
    }

    @Test
    void jpaRepositoriesMustStayInsideThePersistenceAdapter() {
        ArchRule rule = noClasses()
                .that().resideOutsideOfPackage(BASE + ".adapter.out.persistence..")
                .should().dependOnClassesThat().resideInAPackage("org.springframework.data.jpa.repository..")
                .because("Spring Data must not leak past the persistence adapter");

        rule.check(classes);
    }

    @Test
    void noClassMayUseStandardOutput() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + "..")
                .should().accessField(System.class, "out")
                .orShould().accessField(System.class, "err")
                .because("logging goes through SLF4J so it can be collected in production");

        rule.check(classes);
    }
}
