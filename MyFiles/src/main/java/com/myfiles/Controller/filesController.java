package com.myfiles.Controller;

import java.util.List;
import java.util.Map;

import com.myfiles.Dtos.UploadResultDto;
import com.myfiles.Service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

//@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/files/v1")
public class filesController {

	@Autowired
	private FilesService service;

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
	@PostMapping("/upload")
	public List<UploadResultDto> upload(@RequestParam("files") List<MultipartFile> files,
			@RequestParam("section") Selection selection) {
		return service.uploadFiles(files, selection);
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	@DeleteMapping("/{id}")
	public void delete(@PathVariable long id) {
		service.delete(id);
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	@PostMapping("/delete")
	public Map<String, Integer> deleteMany(@RequestBody List<Long> ids) {
		return Map.of("deleted", service.deleteMany(ids));
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'EMPLOYEE')")
	@GetMapping("/all")
	public List<FilesData> all(@RequestParam("section") Selection selection) {
		return service.getall(selection);
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER' ,'EMPLOYEE')")
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

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(type))
				.header(HttpHeaders.CONTENT_DISPOSITION, mode + "; filename=\"" + f.getFileName() + "\"")
				.body(resource);
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER' ,'EMPLOYEE')")
	@GetMapping("/download/{id}")
	public ResponseEntity<Resource> download(@PathVariable long id) {
		return sendFile(id, "attachment");
	}

	@GetMapping("/me")
	public String getCurrentUser(Authentication authentication) {

		String email = authentication.getName();

		String role = authentication.getAuthorities().iterator().next().getAuthority();

		return "Email: " + email + ", Role: " + role;
	}
}