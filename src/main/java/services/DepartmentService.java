package services;

import entities.Department;
import org.springframework.stereotype.Service;
import repositories.DepartmentRepository;

import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }
}
