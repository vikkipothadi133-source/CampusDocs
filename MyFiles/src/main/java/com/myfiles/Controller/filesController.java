package com.myfiles.Controller;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.myfiles.Service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.myfiles.Entity.FilesData;
import com.myfiles.Entity.Selection;
import com.myfiles.Entity.UploadResultDto;
import com.myfiles.Service.serviceImplimentation;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/files/v1")
public class filesController {


    @Autowired
    private FilesService service;


    @PostMapping("/upload")
    public List<UploadResultDto> upload(@RequestParam("files") List<MultipartFile> files,
                                     @RequestParam("section") Selection selection) {
        return service.uploadFiles(files, selection);
    }
    
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
    
    @PostMapping("/delete")
    public Map<String, Integer> deleteMany(@RequestBody List<Long> ids) {
        return Map.of("deleted", service.deleteMany(ids));
    }


    @GetMapping("/all")
    public List<FilesData> all(@RequestParam("section") Selection selection) {
        return service.getall(selection);
    }
    
    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> view(@PathVariable long id) {
        return sendFile(id, "inline");
    }


//    @GetMapping("/view/{id}")
//    public ResponseEntity<Resource> view(@PathVariable long id) {
//        FilesData f = service.getOneFile(id);
//        Resource resource = service.loadResource(f);
//
//        String type = f.getContentType() != null ? f.getContentType() : "application/octet-stream";
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(type))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + f.getFileName() + "\"")
//                .body(resource);
//    }
    
    
    
    private ResponseEntity<Resource> sendFile(long id, String mode) {
        FilesData f = service.getOneFile(id);
        Resource resource = service.loadResource(f);

        String type = f.getContentType() != null ? f.getContentType() : "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(type))
                .header(HttpHeaders.CONTENT_DISPOSITION, mode + "; filename=\"" + f.getFileName() + "\"")
                .body(resource);
    }
    
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable long id) {
        return sendFile(id, "attachment");
    }
    
    
}