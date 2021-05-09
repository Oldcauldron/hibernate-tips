package com.javahelps.jpa.test.first_level_chache_and_query.autoflushing;

import com.javahelps.jpa.test.model.Post;
import com.javahelps.jpa.test.model.PostComment;
import com.javahelps.jpa.test.util.PersistentHelper;

import javax.persistence.EntityManager;

public class Shorty {
    public static void main(String[] args) {
        EntityManager entityManager = PersistentHelper.getEntityManager(new Class[] {Post.class, PostComment.class});
        Post post = new Post("Post 1");
        PostComment postComment = new PostComment("Comment 1");

        entityManager.getTransaction().begin();
        entityManager.persist(post);
        post.addComment(postComment);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        postComment = entityManager.find(PostComment.class, 1L);
        postComment.setReview("Comment 2");

        entityManager.createQuery("UPDATE PostComment pc SET pc.review = 'Comment 3' WHERE pc.id = 1").executeUpdate();
        entityManager.createNativeQuery("update post_comment set review='Comment 4' where id=1").executeUpdate();

        postComment = entityManager.find(PostComment.class, 1L);
        entityManager.getTransaction().commit();

        /* каждый вызванный метод увеличивает счётчик в названии комента на 1
        ожидаем, что будет Comment 4
        однако видим Comment 2, потому что сущность была запрошена из кэша первого уровня
        на данные в кэше первого уровня влияют только изменения, проведённые через состояние объекта*/

        // PostComment{id=1, review='Comment 2', post=Post{id=1, title='Post 1'}}
        System.out.println(postComment);

        // Solution 1
        // можно очистить кэш с помощью entityManager.clear() и загрузятся сущности из БД
        entityManager.getTransaction().begin();
        entityManager.clear();
        postComment = entityManager.find(PostComment.class, 1L);
        entityManager.getTransaction().commit();

        // PostComment{id=1, review='Comment 4', post=Post{id=1, title='Post 1'}}
        System.out.println(postComment);


        // Solution 2
        /*
        //обновляем данные в кэше первого уровня
        System.out.println("Проверка на содержание");
        if (entityManager.contains(postComment)) {
            entityManager.refresh(postComment);
        }
        // PostComment{id=1, review='Comment 4', post=Post{id=1, title='Post 1'}}
        System.out.println(postComment);
         */
    }
}
