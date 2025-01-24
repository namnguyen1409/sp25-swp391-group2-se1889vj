package com.group2.sp25swp391group2se1889vj.common.controller;

import com.group2.sp25swp391group2se1889vj.common.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class FileUploadController {
    private final StorageService storageService;

    @Value("${file.upload-avatar-size}")
    private int avatarSize;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/avatar")
    @ResponseBody
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        int maxAvatarSize = avatarSize * 1024 * 1024;
        if (!storageService.isValidSize(file, maxAvatarSize)) {
            response.put("status", "error");
            response.put("message", "File size exceeds the limit of " + avatarSize + "MB.");
            return ResponseEntity.badRequest().body(response);
        }
        if (!storageService.isImage(file)) {
            response.put("status", "error");
            response.put("message", "File is not an image.");
            return ResponseEntity.badRequest().body(response);
        } else {
            var url = storageService.store(file);
            response.put("status", "success");
            response.put("message", "File uploaded successfully");
            response.put("url", url);
            return ResponseEntity.ok(response);
        }
    }


    @PostMapping("/product")
    @ResponseBody
    public ResponseEntity<?> uploadProductImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        if (!storageService.isImage(file)) {
            response.put("status", "error");
            response.put("message", "File is not an image.");
            return ResponseEntity.badRequest().body(response);
        } else {
            var url = storageService.saveToTemp(file);
            response.put("status", "success");
            response.put("message", "File uploaded successfully");
            response.put("url", url);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> serveFile(@PathVariable String filename) {
        var file = storageService.loadAsResource(filename);
        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/temps/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> serveTempFile(@PathVariable String filename) {
        var file = storageService.loadTempAsResource(filename);
        return ResponseEntity.notFound().build();

    }




}
