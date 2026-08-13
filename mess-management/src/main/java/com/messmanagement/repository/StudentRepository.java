package com.messmanagement.repository;

import com.messmanagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByNameContainingIgnoreCase(String name);

    // Only non-deleted students - used everywhere the app shows "the student list"
    List<Student> findByDeletedFalseOrderByNameAsc();

    List<Student> findByNameContainingIgnoreCaseAndDeletedFalse(String name);
}
