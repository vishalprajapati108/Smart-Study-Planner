package study.planner.smart.study.planner.controller;

import study.planner.smart.study.planner.entity.Topic;
import study.planner.smart.study.planner.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping("/{subjectId}/notes")
    public List<Topic> upload(@PathVariable Long subjectId,
                              @RequestParam("file") MultipartFile file) throws IOException {
        return noteService.upload(subjectId, file);
    }
}