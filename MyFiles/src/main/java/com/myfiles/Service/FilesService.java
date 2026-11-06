package com.myfiles.Service;


import java.io.IOException;
import java.util.List;

import com.myfiles.Dtos.UploadResultDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.myfiles.Entity.FilesData;
import com.myfiles.Entity.Selection;

public interface FilesService {

    FilesData uploadFile(MultipartFile file, Selection selection)
            throws IOException;

    List<UploadResultDto> uploadFiles(
            List<MultipartFile> files, Selection selection);

    void delete(Long id);

    int deleteMany(List<Long> ids);

    List<FilesData> getall(Selection selection);

    FilesData getOneFile(Long id);

    Resource loadResource(FilesData f);
}

