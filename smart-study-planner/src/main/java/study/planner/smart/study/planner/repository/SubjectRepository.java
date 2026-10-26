package study.planner.smart.study.planner.repository;

import study.planner.smart.study.planner.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {}