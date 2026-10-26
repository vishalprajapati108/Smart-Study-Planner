package study.planner.smart.study.planner.controller;

import study.planner.smart.study.planner.dto.ProgressResponse;
import study.planner.smart.study.planner.entity.Subject;
import study.planner.smart.study.planner.entity.Topic;
import study.planner.smart.study.planner.repository.SubjectRepository;
import study.planner.smart.study.planner.repository.TopicRepository;
import study.planner.smart.study.planner.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectRepository subjectRepo;
    private final TopicRepository topicRepo;
    private final ProgressService progressService;

    @PostMapping
    public Subject create(@RequestBody Subject subject) {
        return subjectRepo.save(subject);
    }

    @GetMapping
    public List<Subject> all() {
        return subjectRepo.findAll();
    }

    @GetMapping("/{id}/topics")
    public List<Topic> topics(@PathVariable Long id) {
        return topicRepo.findBySubjectId(id);
    }

    @GetMapping("/{id}/progress")
    public ProgressResponse progress(@PathVariable Long id) {
        return progressService.getProgress(id);
    }
}