package com.chdlsp.alice.controller;

import com.chdlsp.alice.domain.entity.ImageUploadEntity;
import com.chdlsp.alice.interfaces.exception.StorageFileNotFoundException;
import com.chdlsp.alice.service.FileHandlingService;
import com.chdlsp.alice.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/files")
@Slf4j
public class FileUploadController {

    @Autowired
    private FileHandlingService fileHandlingService;

    private final FileUploadService fileUploadService;

    @Autowired
    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        List<String> serveFile = fileUploadService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString()).collect(Collectors.toList());

        model.addAttribute("files", serveFile);

        return "uploadForm";
    }

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<UrlResource> serveFile(@PathVariable String filename) {

        UrlResource file = fileUploadService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {

        String email = (String) session.getAttribute("email");

        // 파일 저장
        fileUploadService.store(file, email);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/files/";
    }

    @GetMapping("/fileName")
    public ResponseEntity<?> getFileInfoByFileName(@RequestParam("fileName") String fileName) {

        // 이미지 명으로 관련 정보 찾기
        ImageUploadEntity fileInfo = fileHandlingService.getFileInfo(fileName);

        log.info("fileInfo : " + fileInfo);

        return ResponseEntity.ok().body(fileInfo);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
