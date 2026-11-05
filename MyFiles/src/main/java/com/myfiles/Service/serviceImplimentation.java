package com.myfiles.Service;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.myfiles.Entity.FilesData;
import com.myfiles.Entity.Selection;
import com.myfiles.Entity.UploadResultDto;
import com.myfiles.Repository.FilesRepository;

@Service
public class serviceImplimentation implements FilesService{

    private final FilesRepository filesRepository;
    private final Path storagePath;



    public serviceImplimentation(FilesRepository repository,
                                 @Value("${file-upload-dir}") String uploadDir) {
        this.filesRepository = repository;
        this.storagePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storagePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload folder !!");
        }
    }


    @Override
    public FilesData uploadFile(MultipartFile file, Selection selection) throws IOException {

        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }


        String checksum = sha256(file);
        if (filesRepository.existsBySelectionAndChecksum(selection, checksum)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate file");
        }


        String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();
        String storageName = UUID.randomUUID() + "_" + originalName;
        Files.copy(file.getInputStream(), storagePath.resolve(storageName));


        int next = filesRepository.maxSeq(selection) + 1;

        FilesData data = new FilesData();
        data.setFileName(originalName);
        data.setStorageName(storageName);
        data.setContentType(file.getContentType());
        data.setSize(file.getSize());
        data.setUploadDate(LocalDateTime.now());
        data.setSelection(selection);
        data.setSeq(next);
        data.setCode(selection.getPrefix() + "-" + String.format("%03d", next));
        data.setChecksum(checksum);

        return filesRepository.save(data);
    }



    @Override
    public  List<UploadResultDto> uploadFiles(List<MultipartFile> files, Selection selection) {


        List<UploadResultDto> results = new ArrayList<>();

        for (MultipartFile file : files) {
            String name = file.getOriginalFilename();

            try {
                FilesData saved = uploadFile(file, selection);
                results.add(new UploadResultDto(name, "UPLOADED", saved.getCode(), null));

            } catch (ResponseStatusException e) {
                if (e.getStatusCode().value() == 409) {
                    results.add(new UploadResultDto(name, "DUPLICATE", null, "Already exists"));
                } else {
                    results.add(new UploadResultDto(name, "FAILED", null, e.getReason()));
                }

            } catch (Exception e) {
                results.add(new UploadResultDto(name, "FAILED", null, "Could not save file"));
            }
        }

        return results;
    }


    @Override
    public void delete(Long id) {
        FilesData f = getOneFile(id);


        Path path = storagePath.resolve(f.getStorageName()).normalize();
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not delete file");
        }


        filesRepository.delete(f);
    }



    @Override
    public int deleteMany(List<Long> ids) {
        int count = 0;
        for (Long id : ids) {
            if (filesRepository.existsById(id)) {
                delete(id);
                count++;
            }
        }
        return count;
    }



    @Override
    public List<FilesData> getall(Selection selection) {
        return filesRepository.findBySelectionOrderByIdDesc(selection);
    }




    @Override
    public FilesData getOneFile(Long id) {
        return filesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
    }



    @Override
    public Resource loadResource(FilesData f) {
        Path filePath = storagePath.resolve(f.getStorageName()).normalize();

        if (!filePath.startsWith(storagePath)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path");
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File missing on disk");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot read file");
        }



    }





    private String sha256(MultipartFile file) throws IOException {
        try (InputStream in = file.getInputStream()) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
