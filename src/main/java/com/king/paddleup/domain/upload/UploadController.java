package com.king.paddleup.domain.upload;

import com.king.paddleup.domain.upload.dto.UploadResponse;
import com.king.paddleup.shared.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/uploads")
public class UploadController {

    private final UploadService uploadService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<UploadResponse>> upload(
            @RequestParam("file") MultipartFile file
    ) {
        UploadResponse response = uploadService.upload(file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success("File uploaded successfully", response));
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<List<UploadResponse>>> uploadBatch(
            @RequestParam("files") List<MultipartFile> files
    ) {
        List<UploadResponse> responses = uploadService.uploadMultiple(files);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success("Files uploaded successfully", responses));
    }
}
