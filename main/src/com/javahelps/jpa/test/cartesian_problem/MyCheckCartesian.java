package com.javahelps.jpa.test.cartesian_problem;

import com.javahelps.jpa.test.util.PersistentHelper;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MyCheckCartesian {
    public static void main(String[] args) {

        entityManagerFactory_(MyCheckCartesian::saveData);
        entityManagerFactory_((entityManager) -> {
            System.out.println("\n\n1_start..............Car car1 = entityManager.find(Car.class, 1)");
//            Car car = entityManager.find(Car.class, 1);
//            String queryWheels = "FROM " + Car.class.getName() + " c LEFT JOIN FETCH c.wheels w ";
//            String queryDoors = "FROM " + Car.class.getName() + " c LEFT JOIN FETCH c.doors d ";
//            List<Car> carList = entityManager.createQuery(queryWheels).getResultList();
//            carList = entityManager.createQuery(queryDoors).getResultList();

//            List<Car> carList = entityManager.createQuery("FROM " + Car.class.getName() + " c LEFT JOIN FETCH c.wheels").getResultList();
            List<Car> carList = entityManager.createQuery("FROM " + Car.class.getName()).getResultList();
            System.out.println("\n\n1_end....................car->.\n");
            carList.forEach(System.out::println);

//            System.out.println("\n\n2_start..............car.getWheels().remove(0);..\n");
//            car.getWheels().remove(0);
//            System.out.println("\n\n2_end................car.getWheels().remove(0); car ->..\n" + car + "\n");
//
//            System.out.println("\n\n3_2.....................flush");
//            entityManager.flush();
        });
//        entityManagerFactory_((entityManager) -> {
//            System.out.println("\n\n4_start...............List<Wheel> listWheels = entityManager.createQuery(FROM Wheel).getResultList()");
//            final List<Wheel> listWheels = entityManager.createQuery("FROM " + Wheel.class.getName()).getResultList();
//            System.out.println("\n\n4_end..............listWheels -> .\n" + listWheels + "\n");
//            entityManager.clear();
//
//            System.out.println("\n\n5..................wheel = entityManager.find(Wheel.class, 1)");
//            Wheel wheel = entityManager.find(Wheel.class, 2);
//
//            System.out.println("\n\n6..................entityManager.remove(wheel);");
//            entityManager.remove(wheel);
//            entityManager.flush();
//
//            System.out.println("\n\n9_start..............car = entityManager.find(Car.class, 1)");
//            Car car = entityManager.find(Car.class, 1);
//            System.out.println("\n\n9_end..............car ->.\n" + car + "\n");
//        });

    }

    public static void entityManagerFactory_(Consumer<EntityManager> cons) {
        EntityManager entityManager = PersistentHelper.getEntityManager(new Class[] {Car.class, Wheel.class, Door.class}, PersistentHelper.DB.POSTGRESQL);
        entityManager.getTransaction().begin();
        cons.accept(entityManager);
        entityManager.getTransaction().commit();
        entityManager.close();
    }
    public static <R> R EMFactory2(Function<EntityManager, R> function) {
        EntityManager entityManager = PersistentHelper.getEntityManager(new Class[] {Car.class, Wheel.class, Door.class}, PersistentHelper.DB.POSTGRESQL);
        entityManager.getTransaction().begin();
        R result =  function.apply(entityManager);
        entityManager.getTransaction().commit();
        entityManager.close();
        return result;
    }

    public static void saveData(EntityManager entityManager) {
        Car car = new Car("BMW");
        Car car2 = new Car("Audi");
        Wheel wheel1 = new Wheel("GoodYR_1");
        Wheel wheel2 = new Wheel("GoodYR_2");
        Wheel wheel3 = new Wheel("GoodYR_3");
        Wheel wheel4 = new Wheel("GoodYR_4");
        Wheel wheel5 = new Wheel("GoodYR_5");
        Door door1 = new Door("left_front");
        Door door2 = new Door("right_front");
        Door door3 = new Door("left_back");
        Door door4 = new Door("right_back");
        Door door5 = new Door("right_back");

        System.out.println("\n\n0_1.................entityManager.persist(car)\n");
        entityManager.persist(car);
        entityManager.persist(car2);
        car.getWheels().addAll(Arrays.asList(wheel1, wheel2, wheel3, wheel4));
        car.getDoors().addAll(Arrays.asList(door1, door2, door3, door4));
        car2.getWheels().add(wheel5);
        car2.getDoors().add(door5);
        System.out.println("\n\n0_2.................car\n" + car + "\n");
        System.out.println("\n\n0_2.................car2\n" + car2 + "\n");
        //entityManager.flush();
    }

    @Entity
    @Table
    static class Car {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        private int id;
        private String carLabel;

        @OneToMany(cascade = CascadeType.ALL)
        @JoinColumn(name = "JoinColumnInCar_car_id_in_wheel")
        @Fetch(FetchMode.JOIN)
        private List<Wheel> wheels = new ArrayList<>();

        @OneToMany(cascade = CascadeType.ALL)
        @JoinColumn(name = "JoinColumnInCar_car_id_in_door")
        @Fetch(FetchMode.SUBSELECT)
        private List<Door> doors = new ArrayList<>();

        public Car() {}
        public Car(String carLabel) {
            this.carLabel = carLabel;
        }

        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getCarLabel() {
            return carLabel;
        }
        public void setCarLabel(String carLabel) {
            this.carLabel = carLabel;
        }
        public List<Wheel> getWheels() {
            return wheels;
        }
        public void setWheels(List<Wheel> wheels) {
            this.wheels = wheels;
        }
        public void addWheel(Wheel wheel) {
            this.wheels.add(wheel);
        }
        public List<Door> getDoors() {
            return doors;
        }
        public void setDoors(List<Door> doors) {
            this.doors = doors;
        }
        public void addDoors(Door door) {
            this.doors.add(door);
        }
        @Override
        public String toString() {
            return "Car{" +
                    "id=" + id +
                    ", carLabel='" + carLabel + '\'' +
                    ", wheels=" + wheels +
                    ", doors=" + doors +
                    '}';
        }
    }

    @Entity
    @Table
    static class Wheel {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private String wheelLabelAndSerialNum;

        public Wheel() {}
        public Wheel(String wheelLabelAndSerialNum) {
            this.wheelLabelAndSerialNum = wheelLabelAndSerialNum;
        }

        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getWheelLabelAndSerialNum() {
            return wheelLabelAndSerialNum;
        }
        public void setWheelLabelAndSerialNum(String wheelLabelAndSerialNum) {
            this.wheelLabelAndSerialNum = wheelLabelAndSerialNum;
        }

        @Override
        public String toString() {
            return "Wheel{" + id + " " + wheelLabelAndSerialNum  + '}';
        }
    }


    @Entity
    @Table
    static class Door {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private String doorsPlace;

        public Door() {
        }
        public Door(String doorsPlace) {
            this.doorsPlace = doorsPlace;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getDoorsPlace() {
            return doorsPlace;
        }
        public void setDoorsPlace(String doorsPlace) {
            this.doorsPlace = doorsPlace;
        }

        @Override
        public String toString() {
            return "Doors{"  + id + " " + doorsPlace +'}';
        }
    }

}
