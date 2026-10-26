package study.planner.smart.study.planner.repository;

import study.planner.smart.study.planner.entity.Topic;
import study.planner.smart.study.planner.entity.TopicStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findBySubjectId(Long subjectId);
    List<Topic> findBySubjectIdAndStatusNot(Long subjectId, TopicStatus status);
    long countBySubjectId(Long subjectId);
    long countBySubjectIdAndStatus(Long subjectId, TopicStatus status);
}