package study.planner.smart.study.planner.dto;

import study.planner.smart.study.planner.entity.StudyPlan;
import java.util.List;

public record TimetableResponse(List<StudyPlan> plans, int unscheduledTopics, String message) {}