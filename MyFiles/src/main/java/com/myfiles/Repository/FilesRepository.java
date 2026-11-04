package com.myfiles.Repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.myfiles.Entity.FilesData;
import com.myfiles.Entity.Selection;

public interface FilesRepository extends JpaRepository<FilesData, Long> {

    List<FilesData> findBySelectionOrderByIdDesc(Selection selection);

    long countBySelection(Selection selection);

    boolean existsBySelectionAndChecksum(Selection selection, String checksum);
    
    @Query("select coalesce(max(f.seq), 0) from FilesData f where f.selection = :selection")
    int maxSeq(@Param("selection") Selection selection);
}
