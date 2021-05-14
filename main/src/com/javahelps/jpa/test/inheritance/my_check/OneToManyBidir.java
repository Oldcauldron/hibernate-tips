package com.javahelps.jpa.test.inheritance.my_check;

import com.javahelps.jpa.test.util.PersistentHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class OneToManyBidir {
    public static void main(String[] args) {
        entityManagerFactory_(OneToManyBidir::saveData);
        entityManagerFactory_((entityManager) -> {
            System.out.println("\n\nem................................." + entityManager);
            System.out.println("\n\n1_start..............Car car1 = entityManager.find(Car.class, 1)");
            Car car = entityManager.find(Car.class, 1);
            System.out.println("\n\n1_end....................car->." + car + "\n");

            System.out.println("\n\n2_start..............car.getWheels().remove(0);..\n");
            final Wheel wheel1 = car.getWheels().remove(0);
            wheel1.setCar(null);
            System.out.println("\n\n2_end................car.getWheels().remove(0); car ->..\n" + car + "\n");

            System.out.println("\n\n3_2.....................flush");
            entityManager.flush();
        });
        entityManagerFactory_((entityManager) -> {
            System.out.println("\n\nem................................." + entityManager);
            System.out.println("\n\n3_3.................car = entityManager.find(Car.class, 1);");
            Car car = entityManager.find(Car.class, 1);
            System.out.println("\n\n3_3......................car->\n" + car);
        });
        entityManagerFactory_((entityManager) -> {
            System.out.println("\n\nem................................." + entityManager);
            System.out.println("\n\n4_start...............List<Wheel> listWheels = entityManager.createQuery(FROM Wheel).getResultList()");
            final List<Wheel> listWheels = entityManager.createQuery("FROM " + Wheel.class.getName()).getResultList();
            System.out.println("\n\n4_end..............listWheels -> .\n" + listWheels + "\n");
            entityManager.clear();

            System.out.println("\n\n5..................wheel = entityManager.find(Wheel.class, 1)");
            Wheel wheel = entityManager.find(Wheel.class, 2);

            System.out.println("\n\n6..................entityManager.remove(wheel);");
            entityManager.remove(wheel);
            entityManager.flush();

            System.out.println("\n\n9_start..............car = entityManager.find(Car.class, 1)");
            Car car = entityManager.find(Car.class, 1);
            System.out.println("\n\n9_end..............car ->.\n" + car + "\n");
        });
    }

    public static void entityManagerFactory_(Consumer<EntityManager> cons) {
        EntityManager entityManager = PersistentHelper.getEntityManager(new Class[] {Car.class, Wheel.class}, PersistentHelper.DB.POSTGRESQL);
        entityManager.getTransaction().begin();
        cons.accept(entityManager);
        entityManager.getTransaction().commit();
        entityManager.close();
    }
    public static <R> R EMFactory2(Function<EntityManager, R> function) {
        EntityManager entityManager = PersistentHelper.getEntityManager(new Class[] {Car.class, Wheel.class}, PersistentHelper.DB.POSTGRESQL);
        entityManager.getTransaction().begin();
        R result =  function.apply(entityManager);
        entityManager.getTransaction().commit();
        entityManager.close();
        return result;
    }

    public static void saveData(EntityManager entityManager) {
        Car car = new Car("BMW");
        Wheel wheel1 = new Wheel("GoodYR_1", car);
        Wheel wheel2 = new Wheel("GoodYR_2", car);
        Wheel wheel3 = new Wheel("GoodYR_3", car);
        Wheel wheel4 = new Wheel("GoodYR_4", car);
        Wheel wheel5 = new Wheel("GoodYR_5", car);

        System.out.println("\n\n0_1.................entityManager.persist(car)\n");
        entityManager.persist(car);
        car.getWheels().addAll(Arrays.asList(wheel1, wheel2, wheel3, wheel4, wheel5));
        System.out.println("\n\n0_2.................car\n" + car + "\n");

    }


    @Entity
    @Table
    static class Car {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private String carLabel;

        @OneToMany(cascade = CascadeType.ALL, mappedBy = "car")
        private List<Wheel> wheels = new ArrayList<>();

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

        @Override
        public String toString() {
            return "Car{" +
                    "id=" + id +
                    ", carLabel='" + carLabel + '\'' +
                    ", wheels=" + wheels +
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

        @ManyToOne
        @JoinColumn(name = "car_id")
        private Car car;

        public Wheel() {}
        public Wheel(String wheelLabelAndSerialNum) {
            this.wheelLabelAndSerialNum = wheelLabelAndSerialNum;
        }
        public Wheel(String wheelLabelAndSerialNum, Car car) {
            this.wheelLabelAndSerialNum = wheelLabelAndSerialNum;
            this.car = car;
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
        public Car getCar() {
            return car;
        }
        public void setCar(Car car) {
            this.car = car;
        }

        @Override
        public String toString() {
            return "Wheel{" + wheelLabelAndSerialNum + '}';
        }
    }

}
