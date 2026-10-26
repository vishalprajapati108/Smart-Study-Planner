package study.planner.smart.study.planner.controller;

import study.planner.smart.study.planner.entity.Topic;
import study.planner.smart.study.planner.entity.TopicStatus;
import study.planner.smart.study.planner.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicRepository topicRepo;

    @PutMapping("/{id}/status")
    public Topic updateStatus(@PathVariable Long id, @RequestParam TopicStatus status) {
        Topic topic = topicRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic nahi mila"));
        topic.setStatus(status);
        return topicRepo.save(topic);
    }
}