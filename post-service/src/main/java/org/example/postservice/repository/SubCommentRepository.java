package org.example.postservice.repository;

import org.example.postservice.Model.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubCommentRepository extends JpaRepository<SubComment,Long> {
    Optional<SubComment> findById(Long id);
}
