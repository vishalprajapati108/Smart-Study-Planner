package study.planner.smart.study.planner.repository;

import study.planner.smart.study.planner.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {}