package com.javahelps.jpa.test.inheritance;

import com.javahelps.jpa.test.util.PersistentHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MappedSuperClass {
    public static void main(String[] args) {
        EntityManager entityManager = PersistentHelper.getEntityManager(new Class[] {Department.class, Manager.class, Expert.class});

        saveData(entityManager);
        entityManager.clear();

        {   //попытка выполнить полиморфный запрос приводит созданию двух отдельных запросов. по одному на каждую таблицу

            System.out.println();
            System.out.println("Before Employee select");
            System.out.println();

            entityManager.getTransaction().begin();

            List<Employee> employees =
                    entityManager.createQuery("FROM " + Employee.class.getName(), Employee.class).getResultList();

            employees.forEach(System.out::println);

            entityManager.getTransaction().commit();

            System.out.println();
            System.out.println("After Employee select");
            System.out.println();

        }


        {   //join fetch одного подтипа к другому

            System.out.println();
            System.out.println("Before Manager select");
            System.out.println();

            entityManager.getTransaction().begin();

            List<Manager> employees =
                    entityManager.createQuery("FROM " + Manager.class.getName() + " m JOIN FETCH m.supervisors", Manager.class).getResultList();

            employees.forEach(System.out::println);

            entityManager.getTransaction().commit();

            System.out.println();
            System.out.println("After Manager select");
            System.out.println();

        }

        entityManager.clear();

        {   //нет возможности связать department с employe с двух сторн. либо делаем дополнительный запрос, либо связываем с manager и expert напрямую
            System.out.println();
            System.out.println("Before Department select");
            System.out.println();

            entityManager.getTransaction().begin();

            List<Department> employees =
                    entityManager.createQuery("SELECT DISTINCT d FROM " + Department.class.getName() + " d JOIN FETCH d.employees", Department.class).getResultList();

            employees.forEach(System.out::println);

            entityManager.getTransaction().commit();

            System.out.println();
            System.out.println("After Department select");
            System.out.println();
        }

    }

    private static void saveData(EntityManager entityManager) {
        entityManager.getTransaction().begin();

        Department department = new Department("department 1", "Russia");

        Manager manager1 = new Manager("manager 1", 100);
        Manager manager2 = new Manager("manager 2", 120);

        Expert expert1 = new Expert("expert 1", 50);
        Expert expert2 = new Expert("expert 1", 110);
        Expert expert3 = new Expert("expert 1", 170);
        Expert expert4 = new Expert("expert 1", 210);

        manager1.addExpert(expert1);
        manager1.addExpert(expert2);
        manager2.addExpert(expert3);
        manager2.addExpert(expert4);

        entityManager.persist(manager1);
        entityManager.persist(manager2);

        entityManager.persist(department);

        expert1.setDepartment(department);
        expert2.setDepartment(department);
        expert3.setDepartment(department);
        expert4.setDepartment(department);

        manager1.setDepartment(department);
        manager2.setDepartment(department);

        entityManager.getTransaction().commit();
    }

    @Entity
    @Table(name = "department")
    static class Department {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        private String country;

        public Department() {
        }

        public Department(String name, String country) {
            this.name = name;
            this.country = country;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Department that = (Department) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(country, that.country);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, country);
        }

        @Override
        public String toString() {
            return "Department{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", country='" + country + '\'' +
                    '}';
        }
    }

    @MappedSuperclass
    static abstract class Employee {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        private Integer salary;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "department_id")
        private Department department;

        public Employee(String name, Integer salary) {
            this.name = name;
            this.salary = salary;
        }

        public Employee() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getSalary() {
            return salary;
        }

        public void setSalary(Integer salary) {
            this.salary = salary;
        }

        public Department getDepartment() {
            return department;
        }

        public void setDepartment(Department department) {
            this.department = department;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Employee employee = (Employee) o;
            return Objects.equals(id, employee.id) &&
                    Objects.equals(name, employee.name) &&
                    Objects.equals(salary, employee.salary);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, salary);
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", salary=" + salary +
                    '}';
        }
    }

    @SuppressWarnings("JpaMissingIdInspection")
    @Entity
    static class Manager extends Employee {

        @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Expert> supervisors = new ArrayList<>();

        public Manager() {
            super();
        }

        public Manager(String name, Integer salary) {
            super(name, salary);
        }

        //заводим специальные методы для добавления и удаления, которые позволяют избавиться от повторного кода
        public void addExpert(Expert expert) {
            this.supervisors.add(expert);
            expert.setManager(this);
        }

        public void removeExpert(Expert expert) {
            this.supervisors.remove(expert);
            expert.setManager(null);
        }

        public List<Expert> getExperts() {
            return supervisors;
        }

        public void setExperts(List<Expert> supervisors) {
            this.supervisors = supervisors;
        }
    }

    @SuppressWarnings("JpaMissingIdInspection")
    @Entity
    static class Expert extends Employee {

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "manager_id")
        private Manager manager;

        public Expert() {
            super();
        }

        public Expert(String name, Integer salary) {
            super(name, salary);
        }

        public Manager getManager() {
            return manager;
        }

        public void setManager(Manager manager) {
            this.manager = manager;
        }

    }
}
