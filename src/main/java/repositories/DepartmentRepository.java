package repositories;

import entities.Department;

import java.util.Optional;

public interface DepartmentRepository {
    Optional<Department> findByName(String name);
}
