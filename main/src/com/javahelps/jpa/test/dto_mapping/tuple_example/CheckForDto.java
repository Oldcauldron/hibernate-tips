package com.javahelps.jpa.test.dto_mapping.tuple_example;

import com.javahelps.jpa.test.model.Post;
import com.javahelps.jpa.test.model.PostComment;
import com.javahelps.jpa.test.util.PersistentHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class CheckForDto {

    public static void main(String[] args) {
        {
            EntityManager entityManager = PersistentHelper.getEntityManager(new Class[]{Post.class, PostComment.class});
            Post post1 = new Post("..........title1");
            Post post2 = new Post("..........title2");

            PostComment postComment1_1 = new PostComment("..........text_review1_from_post1");
            PostComment postComment1_2 = new PostComment("..........text_review2_from_post1");
            PostComment postComment1_3 = new PostComment("..........text_review3_from_post1");
            PostComment postComment1_4 = new PostComment("..........text_review4_from_post1");
            PostComment postComment2_1 = new PostComment("..........text_review1_from_post2");
            PostComment postComment2_2 = new PostComment("..........text_review2_from_post2");

            post1.addComment(postComment1_1);
            post1.addComment(postComment1_2);
            post1.addComment(postComment1_3);
            post1.addComment(postComment1_4);
            post2.addComment(postComment2_1);
            post2.addComment(postComment2_2);

            entityManager.getTransaction().begin();
            entityManager.persist(post1);
            entityManager.persist(post2);
            entityManager.getTransaction().commit();
            entityManager.clear();
            entityManager.close();
        }

        {
            EntityManager entityManager = PersistentHelper.getEntityManager(new Class[]{PostDto.class, PostComment.class});

            entityManager.getTransaction().begin();
            String query = "select concat(p.id, p.title) as idtitle, pc.review as review " +
                           "from post p RIGHT JOIN post_comment pc on p.id = pc.post_id";
//            final List resultList = entityManager.createNativeQuery(query).getResultList();
            final List<Tuple> tupleList = entityManager.createNativeQuery(query, Tuple.class).getResultList();

//            List<Tuple> tupleList = entityManager.createNativeQuery(query, Tuple.class).getResultList();

//            String query = "select p.id as id, p.title as title from Post p";
//            List<Tuple> tupleList = entityManager.createQuery(query, Tuple.class).getResultList();


            for (Tuple tuple : tupleList) {
                System.out.println(tuple);
                //String idtitle = tuple.get("id", Long.class) + tuple.get("title", String.class);
                PostDto postDto = new PostDto(tuple.get("idtitle", String.class), tuple.get("review", String.class));
                System.out.println("\n\n...............\n" + postDto);
            }

            entityManager.getTransaction().commit();
            entityManager.clear();
            entityManager.close();
        }
    }

}

class PostDto {
    private String idtitle;
//    private List<PostComment> comments = new ArrayList<>();
    private String review;
    public PostDto() {
    }

    public PostDto(String idtitle) {
        this.idtitle = idtitle;
    }

    public PostDto(String idtitle, String review) {
        this.idtitle = idtitle;
        this.review = review;
    }

    public String getIdtitle() {
        return idtitle;
    }

    public void setIdtitle(String idtitle) {
        this.idtitle = idtitle;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
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
        return "PostDTO{" +
                ", idtitle='" + idtitle + '\'' +
                ", review=" + review +
                '}';
    }
}
