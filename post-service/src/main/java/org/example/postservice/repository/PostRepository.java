package org.example.postservice.repository;

import org.example.postservice.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllPostsByUserId(Long id);
}
