package com.javahelps.jpa.test.enum_mapping;

import com.javahelps.jpa.test.inheritance.MappedSuperClass;
import com.javahelps.jpa.test.util.PersistentHelper;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        EntityManager entityManager = PersistentHelper.getEntityManager(new Class[] {Person.class});

        Person person = new Person("PersonName", Role.USER, Level.MIDDLE, Sex.MAN);
        entityManager.getTransaction().begin();
        //entityManager.persist(person);
        entityManager.persist(person);
        person.id = 1;
        person.name = "PersonName_Change1";
        entityManager.clear();
        entityManager.getTransaction().commit();
        entityManager.close();

        EntityManager entityManager3 = PersistentHelper.getEntityManager(new Class[] {Person.class});
        entityManager3.getTransaction().begin();
        final Person person2 = entityManager3.find(Person.class, 1);
        System.out.println("3.............." + person2); //Person{id=1, name='PersonName_Change2',
        entityManager3.remove(person2);
        System.out.println("4.............." + person2);
        person2.name = "NewAfterFirstMergeName";
        entityManager3.persist(person2);
        entityManager3.getTransaction().commit();
        entityManager3.close();

        EntityManager entityManager2 = PersistentHelper.getEntityManager(new Class[] {Person.class});
        entityManager2.getTransaction().begin();
        final Person person1 = entityManager2.find(Person.class, 1);
        System.out.println("5.............." + person1);
        entityManager2.getTransaction().commit();
        entityManager2.close();


    }
}

@Entity
@DynamicUpdate
class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String name;

    @Enumerated(EnumType.STRING)
    Role role;

    @Transient
    Level level;
    int levelPresentation;

    Sex sex;

    public Person(String name, Role role, Level level, Sex sex) {
        this.name = name;
        this.role = role;
        this.level = level;
        this.sex = sex;
    }
    public Person() {
    }

    @PrePersist
    public void prePersist() {
        if (level != null) {this.levelPresentation = level.getLevel();}
    }
    @PostLoad
    public void postLoad() {
        if(levelPresentation > 0) {
            this.level = Level.of(this.levelPresentation);
        }
    }
    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", level=" + level +
                ", levelPresentation=" + levelPresentation +
                ", sex=" + sex +
                '}';
    }
}

enum Role {
    ADMIN, USER;
}
enum Level {
    HIGH(1), MIDDLE(2), LOW(3);
    int level;
    private Level(int level) {this.level = level;}
    public int getLevel() {return level;}
    public static Level of(int level) {
        return Stream.of(Level.values())
                .filter(x -> x.getLevel() == level)
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
enum Sex {
    MAN("M"), WOMAN("W");
    String code;
    Sex(String code) {this.code = code;}
    public String getCode() {return code;}
}

@Converter(autoApply = true)
class SexConverter implements AttributeConverter<Sex, String> {
    @Override
    public String convertToDatabaseColumn(Sex sex) {
        if (sex == null) return null;
        return sex.getCode();
    }
    @Override
    public Sex convertToEntityAttribute(String s) {
        if (s == null) return null;
        return Stream.of(Sex.values())
                .filter(x -> x.getCode().equals(s))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
