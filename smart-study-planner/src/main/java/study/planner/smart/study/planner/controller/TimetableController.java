package study.planner.smart.study.planner.controller;

import study.planner.smart.study.planner.dto.TimetableResponse;
import study.planner.smart.study.planner.entity.StudyPlan;
import study.planner.smart.study.planner.repository.StudyPlanRepository;
import study.planner.smart.study.planner.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;
    private final StudyPlanRepository planRepo;

    @PostMapping("/subjects/{id}/timetable")
    public TimetableResponse generate(@PathVariable Long id,
                                      @RequestParam(defaultValue = "2") double dailyHours,
                                      @RequestParam(defaultValue = "18:00") String startTime) {
        return timetableService.generate(id, dailyHours, LocalTime.parse(startTime));
    }

    @GetMapping("/subjects/{id}/timetable")
    public List<StudyPlan> timetable(@PathVariable Long id) {
        return planRepo.findByTopicSubjectIdOrderByStudyDateAscStartTimeAsc(id);
    }

    @GetMapping("/timetable/today")
    public List<StudyPlan> today() {
        return planRepo.findByStudyDateOrderByStartTime(LocalDate.now());
    }

    @PutMapping("/timetable/{planId}/complete")
    public StudyPlan complete(@PathVariable Long planId) {
        StudyPlan plan = planRepo.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan nahi mila"));
        plan.setCompleted(true);
        return planRepo.save(plan);
    }
}