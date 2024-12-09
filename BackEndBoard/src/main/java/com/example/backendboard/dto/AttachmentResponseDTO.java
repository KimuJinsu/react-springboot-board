package com.example.backendboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttachmentResponseDTO {
    private Long id;
    private String realFileName;
    private String s3Url; // S3 URL 추가
}


//package com.example.backendboard.dto;
//
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//public class AttachmentDTO {
//    private Long id;
//    private String realFileName;
//
//}