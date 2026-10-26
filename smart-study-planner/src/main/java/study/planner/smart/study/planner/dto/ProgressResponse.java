package study.planner.smart.study.planner.dto;

public record ProgressResponse(long totalTopics, long doneTopics,
                               double percentage, int remainingMinutes) {}