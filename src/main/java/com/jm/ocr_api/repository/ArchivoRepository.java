package com.jm.ocr_api.repository;

import com.jm.ocr_api.model.Archivo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArchivoRepository extends MongoRepository<Archivo, Integer> {

}
