package com.messmanagement.service;

import com.messmanagement.exception.ResourceNotFoundException;
import com.messmanagement.model.Student;
import com.messmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handles all student-related business logic: create, read, update,
 * delete, and name search. Controllers should never talk to the
 * repository directly - they go through this service.
 *
 * "Delete" is a soft delete: the row is never removed from the
 * database. It is only flagged as deleted, so it disappears from
 * lists and the dashboard, but the record and all its payment
 * history stay intact permanently.
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findByDeletedFalseOrderByNameAsc();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Transactional
    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Long id, Student updated) {
        Student existing = getStudentById(id);
        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());
        existing.setEmail(updated.getEmail());
        existing.setAddress(updated.getAddress());
        return studentRepository.save(existing);
    }

    /**
     * Soft delete: marks the student as deleted instead of removing
     * the row, so payment history and records are preserved forever.
     */
    @Transactional
    public void deleteStudent(Long id) {
        Student existing = getStudentById(id);
        existing.setDeleted(true);
        studentRepository.save(existing);
    }

    public List<Student> searchByName(String name) {
        if (name == null || name.isBlank()) {
            return getAllStudents();
        }
        return studentRepository.findByNameContainingIgnoreCaseAndDeletedFalse(name.trim());
    }
}
