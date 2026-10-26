package study.planner.smart.study.planner.service;

import study.planner.smart.study.planner.entity.Note;
import study.planner.smart.study.planner.entity.Subject;
import study.planner.smart.study.planner.entity.Topic;
import study.planner.smart.study.planner.repository.NoteRepository;
import study.planner.smart.study.planner.repository.SubjectRepository;
import study.planner.smart.study.planner.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final SubjectRepository subjectRepo;
    private final NoteRepository noteRepo;
    private final TopicRepository topicRepo;
    private final TopicExtractorService extractor;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    public List<Topic> upload(Long subjectId, MultipartFile file) throws IOException {
        Subject subject = subjectRepo.findById(subjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject nahi mila"));

        String original = file.getOriginalFilename() == null ? "notes" : file.getOriginalFilename();
        Path dir = Path.of(uploadDir);
        Files.createDirectories(dir);
        Path saved = dir.resolve(UUID.randomUUID() + "_" + Path.of(original).getFileName());
        Files.copy(file.getInputStream(), saved);

        String text = readText(saved, original);
        if (text.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "File se text nahi mila (scanned PDF ho sakti hai)");
        }

        Note note = new Note();
        note.setSubject(subject);
        note.setFileName(original);
        note.setFilePath(saved.toString());
        noteRepo.save(note);

        List<Topic> topics = extractor.extract(text);
        topics.forEach(t -> {
            t.setSubject(subject);
            t.setNote(note);
        });
        return topicRepo.saveAll(topics);
    }

    private String readText(Path path, String originalName) throws IOException {
        String name = originalName.toLowerCase();
        if (name.endsWith(".pdf")) {
            try (PDDocument doc = Loader.loadPDF(path.toFile())) {
                return new PDFTextStripper().getText(doc);
            }
        }
        if (name.endsWith(".txt")) {
            return Files.readString(path);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Abhi sirf PDF aur TXT supported hai");
    }
}