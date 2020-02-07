package com.javahelps.jpa.test.hash_map_mapping;

import com.javahelps.jpa.test.util.PersistentHelper;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ManyToOneRelationV2 {
    public static void main(String[] args) {
        EntityManager entityManager = PersistentHelper.getEntityManager(new Class[] {Order.class, Seller.class, Item.class});

        saveData(entityManager);
        entityManager.clear();

        {
            System.out.println();
            System.out.println("Before order loaded by find method");
            System.out.println();

            entityManager.getTransaction().begin();

            Order order = entityManager.find(Order.class, 1L);

            for (Map.Entry<Seller, Item> entry : order.getSellerItemMap().entrySet()) {
                System.out.println(entry.getKey().getName());
                System.out.println(entry.getValue().getName());
            }

            entityManager.getTransaction().commit();

            System.out.println();
            System.out.println("After order loaded by find method");
            System.out.println();
        }

        entityManager.clear();

        {   //join fetch подгружает атрибут значения, и из-за этого, хибернейт не может самостоятельно
            //построить граф, в котором можно было бы выбрать сразу и ключ. n+1
            System.out.println();
            System.out.println("Before order loaded by jpql with join  fetch");
            System.out.println();

            entityManager.getTransaction().begin();

            Order order = entityManager.createQuery(
                    "SELECT o FROM "+Order.class.getName()+" o " +
                            "JOIN FETCH o.sellerItemMap " +
                            "WHERE o.id =:id",
                    Order.class
            )
                    .setParameter("id", 1L)
                    .getSingleResult();

            for (Map.Entry<Seller, Item> entry : order.getSellerItemMap().entrySet()) {
                System.out.println(entry.getKey().getName());
                System.out.println(entry.getValue().getName());
            }

            entityManager.getTransaction().commit();

            System.out.println();
            System.out.println("After order loaded");
            System.out.println();
        }

        entityManager.clear();

        {   //то же самое, что и в верхнем join fetch
            System.out.println();
            System.out.println("Before order loaded by one graph");
            System.out.println();

            entityManager.getTransaction().begin();

            EntityGraph<Order> entityGraph = entityManager.createEntityGraph(Order.class);
            entityGraph.addAttributeNodes("sellerItemMap");

            Map<String, Object> hints = new HashMap<>();
            hints.put("javax.persistence.loadgraph", entityGraph);

            Order order = entityManager.find(Order.class, 1L, hints);

            for (Map.Entry<Seller, Item> entry : order.getSellerItemMap().entrySet()) {
                System.out.println(entry.getKey().getName());
                System.out.println(entry.getValue().getName());
            }

            entityManager.getTransaction().commit();

            System.out.println();
            System.out.println("After order loaded by one graph");
            System.out.println();
        }

        entityManager.clear();

        {   //использование сабграфа полностью решает эту проблему
            System.out.println();
            System.out.println("Before order loaded by graph with subgraph");
            System.out.println();

            entityManager.getTransaction().begin();

            EntityGraph<Order> entityGraph = entityManager.createEntityGraph(Order.class);
            Subgraph<Item> itemGraph = entityGraph.addSubgraph("sellerItemMap");
            itemGraph.addAttributeNodes("seller");

            Map<String, Object> hints = new HashMap<>();
            hints.put("javax.persistence.loadgraph", entityGraph);

            Order order = entityManager.find(Order.class, 1L, hints);

            for (Map.Entry<Seller, Item> entry : order.getSellerItemMap().entrySet()) {
                System.out.println(entry.getKey().getName());
                System.out.println(entry.getValue().getName());
            }

            entityManager.getTransaction().commit();

            System.out.println();
            System.out.println("After order loaded by graph with subgraph");
            System.out.println();
        }
    }

    private static void saveData(EntityManager entityManager) {
        entityManager.getTransaction().begin();

        Seller seller1 = new Seller("seller 1");
        Item item1 = new Item("item 1");
        Seller seller2 = new Seller("seller 2");
        Item item2 = new Item("item 2");
        Seller seller3 = new Seller("seller 3");
        Item item3 = new Item("item 3");

        Order order = new Order("order 1");

        entityManager.persist(seller1);
        entityManager.persist(seller2);
        entityManager.persist(seller3);

        entityManager.persist(order);

        order.addSellerAndItem(seller1, item1);
        order.addSellerAndItem(seller2, item2);
        order.addSellerAndItem(seller3, item3);

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);

        entityManager.getTransaction().commit();
    }

    @Entity
    @Table(name = "orders")
    public static class Order {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        @Column(name = "name")
        private String name;

        @OneToMany(mappedBy="order")
        @MapKeyJoinColumn(name="seller_id")
        private Map<Seller, Item> sellerItemMap = new HashMap<>();

        public Order() {
        }

        public Order(String name) {
            this.name = name;
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

        public Map<Seller, Item> getSellerItemMap() {
            return sellerItemMap;
        }

        public void setSellerItemMap(Map<Seller, Item> sellerItemMap) {
            this.sellerItemMap = sellerItemMap;
        }

        public void addSellerAndItem(Seller seller, Item item) {
            item.setSeller(seller);
            item.setOrder(this);

            this.sellerItemMap.put(seller, item);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Order order = (Order) o;
            return id == order.id &&
                    Objects.equals(name, order.name) &&
                    Objects.equals(sellerItemMap, order.sellerItemMap);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, sellerItemMap);
        }
    }

    @Entity
    @Table(name = "seller")
    public static class Seller {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        @Column(name = "name")
        private String name;

        public Seller() {
        }

        public Seller(String name) {
            this.name = name;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Seller seller = (Seller) o;
            return id == seller.id &&
                    Objects.equals(name, seller.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    @Entity
    @Table(name = "item")
    public static class Item {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        @Column(name = "name")
        private String name;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id")
        private Order order;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "seller_id")
        private Seller seller;

        public Item() {
        }

        public Item(String name) {
            this.name = name;
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

        public Seller getSeller() {
            return seller;
        }

        public void setSeller(Seller seller) {
            this.seller = seller;
        }

        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return id == item.id &&
                    Objects.equals(name, item.name) &&
                    Objects.equals(seller, item.seller);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, seller);
        }
    }
}
