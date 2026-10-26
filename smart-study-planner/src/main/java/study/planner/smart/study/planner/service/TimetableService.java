package study.planner.smart.study.planner.service;

import study.planner.smart.study.planner.dto.TimetableResponse;
import study.planner.smart.study.planner.entity.StudyPlan;
import study.planner.smart.study.planner.entity.Subject;
import study.planner.smart.study.planner.entity.Topic;
import study.planner.smart.study.planner.entity.TopicStatus;
import study.planner.smart.study.planner.repository.StudyPlanRepository;
import study.planner.smart.study.planner.repository.SubjectRepository;
import study.planner.smart.study.planner.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final SubjectRepository subjectRepo;
    private final TopicRepository topicRepo;
    private final StudyPlanRepository planRepo;

    @Transactional
    public TimetableResponse generate(Long subjectId, double dailyHours, LocalTime startTime) {
        Subject subject = subjectRepo.findById(subjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject nahi mila"));

        LocalDate today = LocalDate.now();
        if (subject.getExamDate() == null || !subject.getExamDate().isAfter(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Subject ki exam date aaj ke baad ki honi chahiye");
        }
        if (dailyHours <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dailyHours 0 se zyada hona chahiye");
        }

        // purana adhura timetable hatao
        planRepo.deleteByTopicSubjectIdAndCompletedFalse(subjectId);

        List<Topic> pending = new ArrayList<>(
                topicRepo.findBySubjectIdAndStatusNot(subjectId, TopicStatus.DONE));
        pending.sort(Comparator.comparing(Topic::getImportance)); // HIGH -> MEDIUM -> LOW

        int dailyMinutes = (int) (dailyHours * 60);
        LocalDate lastStudyDay = subject.getExamDate().minusDays(1);
        LocalDate day = today;
        int used = 0;
        int unscheduled = 0;
        List<StudyPlan> plans = new ArrayList<>();

        for (Topic topic : pending) {
            int remaining = topic.getEstimatedMinutes();
            while (remaining > 0) {
                if (day.isAfter(lastStudyDay)) {
                    unscheduled++;
                    break;
                }
                int slot = Math.min(remaining, dailyMinutes - used);
                StudyPlan plan = new StudyPlan();
                plan.setTopic(topic);
                plan.setStudyDate(day);
                plan.setStartTime(startTime.plusMinutes(used));
                plan.setEndTime(startTime.plusMinutes(used + slot));
                plans.add(plan);

                used += slot;
                remaining -= slot;
                if (used >= dailyMinutes) {
                    day = day.plusDays(1);
                    used = 0;
                }
            }
        }

        List<StudyPlan> saved = planRepo.saveAll(plans);
        String msg = unscheduled == 0
                ? "Timetable ban gaya"
                : unscheduled + " topics exam se pehle fit nahi hue, daily hours badhaiye";
        return new TimetableResponse(saved, unscheduled, msg);
    }
}