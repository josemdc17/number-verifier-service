package com.jm.ocr_api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService{

    @Value("${storage.location}")
    private String storageLocation;

    @PostConstruct
    @Override
    public void init() { //inicia y crea el directorio
        try {
            Files.createDirectories(Paths.get(storageLocation));
        }catch (IOException e){
            throw new RuntimeException("No se puede inicializar el storage location", e);
        }
    }

    @Override
    public String store(MultipartFile file) { //guardar el archivo
        String filename = file.getOriginalFilename();

        if (file.isEmpty()){
            throw new RuntimeException("Error, está vacío el archivo: " + filename);
        }

        try {
            InputStream inputStream = file.getInputStream();
            Files.copy(inputStream, Paths.get(storageLocation).resolve(filename), StandardCopyOption.REPLACE_EXISTING);

        }catch (IOException e){
            throw new RuntimeException("Error, no se pudo guardar el archivo: " + filename);
        }
        return filename; //retorna el nombre del archivo como se guarda fisicamente y futuramente en la db
    }

    @Override
    public Path load(String filename) { //retornar la ruta del archivo
        return Paths.get(storageLocation).resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) { //retorna o carga el recurso
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()){
                return resource;
            }else{
                throw new RuntimeException("No se puede cargar el archivo: " + filename);
            }

        }catch (MalformedURLException e){
            throw new RuntimeException("No se puede cargar el archivo: " + filename, e);
        }

    }

    @Override
    public void delete(String filename) {
        Path file = load(filename);
        try {
            FileSystemUtils.deleteRecursively(file);
        }catch (IOException e){
            throw new RuntimeException("No se puede eliminar el archivo: " + filename, e);
        }
    }
}
