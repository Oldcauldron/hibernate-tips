package com.javahelps.jpa.test.n_plus_1_problem_one_to_many.my_check;

import com.javahelps.jpa.test.model.Post;
import com.javahelps.jpa.test.model.PostComment;
import com.javahelps.jpa.test.util.PersistentHelper;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import java.util.List;

public class CheckN_1 {
    public static void main(String[] args) {

        {
            EntityManager entityManager = PersistentHelper.getEntityManager(new Class[]{Post.class, PostComment.class});
            Post post1 = new Post("..........title1");
//            Post post2 = new Post("..........title2");

            PostComment postComment1_1 = new PostComment("..........text_review1_from_post1");
            PostComment postComment1_2 = new PostComment("..........text_review2_from_post1");
            PostComment postComment1_3 = new PostComment("..........text_review3_from_post1");
            PostComment postComment1_4 = new PostComment("..........text_review4_from_post1");
//            PostComment postComment2_1 = new PostComment("..........text_review1_from_post2");
//            PostComment postComment2_2 = new PostComment("..........text_review2_from_post2");

            post1.addComment(postComment1_1);
            post1.addComment(postComment1_2);
            post1.addComment(postComment1_3);
            post1.addComment(postComment1_4);
//            post2.addComment(postComment2_1);
//            post2.addComment(postComment2_2);

            entityManager.getTransaction().begin();
            entityManager.persist(post1);
//            entityManager.persist(post2);
            entityManager.getTransaction().commit();
            entityManager.clear();
            entityManager.close();
        }

//        {
//            EntityManager entityManager = PersistentHelper.getEntityManager(new Class[]{Post.class, PostComment.class});
//
//            System.out.println("\n1................post1 = entityManager.createQuery(select p from Post p join fetch p.comments where p.id = 1, Post.class).getSingleResult()");
//            Post post1 = entityManager.createQuery("select p from Post p join fetch p.comments where p.id = 1", Post.class).getSingleResult();
//
//            System.out.println("\n2................Post post2 = entityManager.find(Post.class, 2L);");
//            Post post2 = entityManager.find(Post.class, 2L);
//
//            System.out.println("\n\n3..............print post1..");
//            System.out.println(post1);
//            System.out.println("\n\n4..............print post 2..");
//            System.out.println(post2);
//
//        }

//        {
//            EntityManager entityManager = PersistentHelper.getEntityManager(new Class[]{Post.class, PostComment.class});
//
//            System.out.println("\n1................");
//            entityManager.getTransaction().begin();
//            final Post post1 = entityManager.find(Post.class, 1L);
//            entityManager.getTransaction().commit();
//            entityManager.clear();
//            entityManager.close();
//            //System.out.println(post1.getComments());
//        }



        {
            EntityManager entityManager = PersistentHelper.getEntityManager(new Class[]{Post.class, PostComment.class});

            System.out.println("\n1................List<Post> postList = entityManager.createQuery(\"select p from Post p join fetch p.comments\", Post.class).getResultList();");
            List<Post> postList = entityManager.createQuery("select p from Post p join fetch p.comments", Post.class).getResultList();
//            List<Post> postList = entityManager.createQuery("select distinct p from Post p", Post.class).getResultList();

            System.out.println("\n\n3..............print post1..");
            for (Post post: postList) {
                System.out.println(post);
            }

        }

    }

}

    /*

1................Post post1 = entityManager.createQuery(...join fetch p.comments..)
    select
    post0_.id as id1_0_0_,
    comments1_.id as id1_1_1_,
    post0_.title as title2_0_0_,
    comments1_.post_id as post_id3_1_1_,
    comments1_.review as review2_1_1_,
    comments1_.post_id as post_id3_1_0__,
    comments1_.id as id1_1_0__
            from
    post post0_
    inner join
    post_comment comments1_
    on post0_.id=comments1_.post_id
            where
    post0_.id=1

2................Post post2 = entityManager.find(Post.class, 2L);
    select
    post0_.id as id1_0_0_,
    post0_.title as title2_0_0_
            from
    post post0_
    where
    post0_.id=?

3..............print post1..
    Post{id=1, title='..........title1', comments=[PostComment{id=1, review='..........text_review1_from_post1', post_id=1}, PostComment{id=2, review='..........text_review2_from_post1', post_id=1}, PostComment{id=3, review='..........text_review3_from_post1', post_id=1}, PostComment{id=4, review='..........text_review4_from_post1', post_id=1}]}


4..............print post 2..
    select
    comments0_.post_id as post_id3_1_0_,
    comments0_.id as id1_1_0_,
    comments0_.id as id1_1_1_,
    comments0_.post_id as post_id3_1_1_,
    comments0_.review as review2_1_1_
            from
    post_comment comments0_
    where
    comments0_.post_id=?
    Post{id=2, title='..........title2', comments=[PostComment{id=5, review='..........text_review1_from_post2', post_id=2}, PostComment{id=6, review='..........text_review2_from_post2', post_id=2}]}


     */