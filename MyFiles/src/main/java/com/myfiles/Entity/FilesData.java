package com.myfiles.Entity;



import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "My-files")
public class FilesData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String code;                 // tec-001, std-001, prcpl-001

    @Column(name = "section_name")
    @Enumerated(EnumType.STRING)
    private Selection selection;         // Teacher, Student, Principal

    private String fileName;
    private String storageName;
    private String contentType;
    private long size;
    private LocalDateTime uploadDate;

    @Column(length = 64)
    private String checksum;             // SHA-256
    
    private Integer seq;}
