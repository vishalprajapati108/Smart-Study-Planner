package study.planner.smart.study.planner.service;

import study.planner.smart.study.planner.entity.Importance;
import study.planner.smart.study.planner.entity.Topic;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TopicExtractorService {

    private static final Pattern KEYWORDS = Pattern.compile(
            "important|definition|formula|remember|note|exam|key point|must|diagram|theorem",
            Pattern.CASE_INSENSITIVE);

    public List<Topic> extract(String text) {
        List<Topic> topics = new ArrayList<>();
        String title = "Introduction";
        StringBuilder body = new StringBuilder();

        for (String line : text.split("\\R")) {
            String t = line.trim();
            if (t.isEmpty()) continue;
            if (isHeading(t)) {
                addTopic(topics, title, body.toString());
                title = t;
                body.setLength(0);
            } else {
                body.append(t).append('\n');
            }
        }
        addTopic(topics, title, body.toString());
        return topics;
    }

    private boolean isHeading(String t) {
        if (t.length() > 70 || t.endsWith(".") || t.endsWith(",")) return false;
        boolean keywordHeading = t.matches("(?i)^(chapter|unit|topic|module|lesson)\\b.*");
        boolean numbered = t.matches("^\\d+(\\.\\d+)*[.)]?\\s+\\p{L}.*");
        boolean allCaps = t.matches(".*\\p{L}.*") && t.equals(t.toUpperCase());
        return keywordHeading || numbered || allCaps;
    }

    private void addTopic(List<Topic> topics, String title, String content) {
        if (content.isBlank()) return;

        // bahut lamba content ho to chhote parts mein todo
        if (content.length() <= 3000) {
            topics.add(build(title, content));
            return;
        }
        StringBuilder chunk = new StringBuilder();
        int part = 1;
        for (String line : content.split("\n")) {
            chunk.append(line).append('\n');
            if (chunk.length() >= 2000) {
                topics.add(build(title + " (Part " + part++ + ")", chunk.toString()));
                chunk.setLength(0);
            }
        }
        if (!chunk.isEmpty()) {
            topics.add(build(title + " (Part " + part + ")", chunk.toString()));
        }
    }

    private Topic build(String title, String content) {
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setContent(content);

        int score = 0;
        Matcher m = KEYWORDS.matcher(content);
        while (m.find()) score++;
        topic.setImportance(score >= 3 ? Importance.HIGH : score >= 1 ? Importance.MEDIUM : Importance.LOW);

        int words = content.split("\\s+").length;
        topic.setEstimatedMinutes(Math.max(10, Math.min(90, words / 40)));
        return topic;
    }
}