package com.jm.ocr_api.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.convert.ThreeTenBackPortConverters;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "text_files")
public class Archivo {

    @Id
    public String id;
    public String nombre;
    public LocalDateTime fecha;
}
