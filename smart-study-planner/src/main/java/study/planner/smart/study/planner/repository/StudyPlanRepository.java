package study.planner.smart.study.planner.repository;

import study.planner.smart.study.planner.entity.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {
    List<StudyPlan> findByStudyDateOrderByStartTime(LocalDate date);
    List<StudyPlan> findByTopicSubjectIdOrderByStudyDateAscStartTimeAsc(Long subjectId);
    void deleteByTopicSubjectIdAndCompletedFalse(Long subjectId);
}