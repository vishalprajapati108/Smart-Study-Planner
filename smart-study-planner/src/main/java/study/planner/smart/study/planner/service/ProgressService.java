package study.planner.smart.study.planner.service;


import study.planner.smart.study.planner.dto.ProgressResponse;
import study.planner.smart.study.planner.entity.Topic;
import study.planner.smart.study.planner.entity.TopicStatus;
import study.planner.smart.study.planner.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final TopicRepository topicRepo;

    public ProgressResponse getProgress(Long subjectId) {
        long total = topicRepo.countBySubjectId(subjectId);
        long done = topicRepo.countBySubjectIdAndStatus(subjectId, TopicStatus.DONE);
        double pct = total == 0 ? 0 : Math.round(done * 10000.0 / total) / 100.0;

        int remaining = topicRepo.findBySubjectIdAndStatusNot(subjectId, TopicStatus.DONE)
                .stream().mapToInt(Topic::getEstimatedMinutes).sum();

        return new ProgressResponse(total, done, pct, remaining);
    }
}