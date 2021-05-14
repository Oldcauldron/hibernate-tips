package com.javahelps.jpa.test.id_strategy;

import com.javahelps.jpa.test.util.PersistentHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CheckSelect {
    public static void main(String[] args) throws InterruptedException {

        new Thread(ForCheckSeq::go).start();
        Thread.sleep(5000);
        new Thread(ForCheckSeq::go2).start();
        new Thread(ForCheckSeq::go3).start();
        new Thread(ForCheckSeq::go2).start();
//        TimeUnit.SECONDS.sleep(5);
//        new Thread(ForCheckSeq::go).start();

//        EntityManager entityManager = PersistentHelper.getEntityManager(new Class[]{Post.class, PostComment.class}, PersistentHelper.DB.POSTGRESQL);
//        //org.apache.log4j.BasicConfigurator.configure();
//        {
//            String seq = "CREATE SEQUENCE IF NOT EXISTS SEQ_POST_NAME INCREMENT BY 1 MINVALUE 1 START WITH 1 CACHE 1";
//            entityManager.getTransaction().begin();
//            entityManager.createNativeQuery(seq);
//            entityManager.getTransaction().commit();
//
//            Post post1 = new Post("title1");
//            Post post2 = new Post("title2");
//            Post post3 = new Post("title3");
//            Post post4 = new Post("title4");
//            Post post5 = new Post("title5");
//            Post post6 = new Post("title6");
//
//            entityManager.getTransaction().begin();
//            entityManager.persist(post1);
//            entityManager.persist(post2);
//            entityManager.persist(post3);
//            entityManager.persist(post4);
//            entityManager.persist(post5);
//            entityManager.persist(post6);
//            entityManager.getTransaction().commit();
//            entityManager.close();
//        }
    }
}

class ForCheckSeq {
    public static void go() {
        EntityManager entityManager2 = PersistentHelper.getEntityManager(new Class[]{Post.class, PostComment.class}, PersistentHelper.DB.POSTGRESQL);
        Post post1_2 = new Post("title1");
        Post post2_2 = new Post("title2");
        Post post3_2 = new Post("title3");
        entityManager2.getTransaction().begin();
        entityManager2.persist(post1_2);
        entityManager2.persist(post2_2);
        entityManager2.persist(post3_2);
        entityManager2.flush();
        entityManager2.getTransaction().commit();
        entityManager2.close();
    }

    public static void go2() {
        EntityManager entityManager = PersistentHelper.getEntityManager(new Class[]{Post.class, PostComment.class}, PersistentHelper.DB.POSTGRESQL);
        Post post1_2 = new Post("title1");
        Post post2_2 = new Post("title2");
        Post post3_2 = new Post("title3");
        Post post4_2 = new Post("title4");
        Post post5_2 = new Post("title5");
        Post post6_2 = new Post("title6");

        entityManager.getTransaction().begin();
        entityManager.persist(post1_2);
        entityManager.persist(post2_2);
        entityManager.persist(post3_2);
        entityManager.persist(post4_2);
        entityManager.persist(post5_2);
        entityManager.persist(post6_2);
        entityManager.flush();
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public static void go3() {
        EntityManager entityManager = PersistentHelper.getEntityManager(new Class[]{Post.class, PostComment.class}, PersistentHelper.DB.POSTGRESQL);
        Post post1_2 = new Post("title1");
        Post post2_2 = new Post("title2");
        Post post3_2 = new Post("title3");
        Post post4_2 = new Post("title4");
        Post post5_2 = new Post("title5");
        Post post6_2 = new Post("title6");

        entityManager.getTransaction().begin();
        entityManager.persist(post1_2);
        entityManager.persist(post2_2);
        entityManager.persist(post3_2);
        entityManager.persist(post4_2);
        entityManager.persist(post5_2);
        entityManager.persist(post6_2);
        entityManager.flush();
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}


@Entity(name = "Post")
@Table(name = "post")
class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_generator")
    @SequenceGenerator(name = "sequence_generator", sequenceName = "SEQ_POST_NAME", allocationSize = 2)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> comments = new ArrayList<>();

    public Post() {
    }

    public Post(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PostComment> getComments() {
        return comments;
    }

    public void setComments(List<PostComment> comments) {
        this.comments = comments;
    }

    public void addComment(PostComment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(PostComment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }

    @PrePersist
    private void prePersist() {
        System.out.println("Post pre persist");
    }

    @PreUpdate
    private void preUpdate() {
        System.out.println("Post pre update");
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}


@Entity(name = "PostComment")
@Table(name = "post_comment")
class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String review;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;

    public PostComment() {
    }

    public PostComment(String review) {
        this.review = review;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @PrePersist
    private void prePersist() {
        System.out.println("PostComment pre persist");
    }

    @PreUpdate
    private void preUpdate() {
        System.out.println("PostComment pre update");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostComment)) return false;
        return id != null && id.equals(((PostComment) o).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "PostComment{" +
                "id=" + id +
                ", review='" + review + '\'' +
                ", post=" + post +
                '}';
    }
}
