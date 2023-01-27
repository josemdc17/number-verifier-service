package com.jm.ocr_api.controller;

import com.jm.ocr_api.model.Archivo;
import com.jm.ocr_api.repository.ArchivoRepository;
import com.jm.ocr_api.services.FileSystemStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
public class VerifierController {

    @Autowired
    public ArchivoRepository archivoRepository;

    @Autowired
    public FileSystemStorageService fileSystemStorageService;

    @GetMapping("/numbers/{fileName}")
    List<String> numbers(@PathVariable("fileName") String fileName) throws FileNotFoundException {

        String pathToFile = "D:\\desarrollo\\java_projects\\ocr_api\\mediafiles\\" + fileName;
        FileReader myFile = new FileReader(pathToFile);
        BufferedReader br = new BufferedReader(myFile);

        List<String> arrayFromReadFile = br.lines()
                .filter(c -> !c.equals(""))
                .collect(Collectors.toList());

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, Integer> numberDictionary = new HashMap<>();
        numberDictionary.put(" _ | ||_|", 0);
        numberDictionary.put("     |  |", 1);
        numberDictionary.put(" _  _||_ ", 2);
        numberDictionary.put(" _  _| _|", 3);
        numberDictionary.put("   |_|  |", 4);
        numberDictionary.put(" _ |_  _|", 5);
        numberDictionary.put(" _ |_ |_|", 6);
        numberDictionary.put(" _   |  |", 7);
        numberDictionary.put(" _ |_||_|", 8);
        numberDictionary.put(" _ |_| _|", 9);

        StringBuilder numberBuilder = new StringBuilder();
        List<String> arrayFilteredFromFile = new ArrayList<>();

        int augI = 0;
        int augJ = 0;
        int myLength = arrayFromReadFile.size() * 3;

        for (int x = 0; x < myLength; x++) {
            for (int i = augI; i < 3 + augI; i++) {
                for (int j = augJ; j < 3 + augJ; j++) {
                    numberBuilder.append(arrayFromReadFile.get(i).charAt(j));
                }
            }

            arrayFilteredFromFile.add(String.valueOf(numberBuilder));
            numberBuilder.delete(0, 10);

            if (augJ < 24) {
                augJ += 3;
            } else {
                augJ = 0;
            }

            if (x % 9 == 8) {
                augI += 3;
            }

        }

        // CONVERTING TO INT AND ADDING TO NEW ARRAY

        List<String> creditCardNumber = new ArrayList<>();
        StringBuilder newStr = new StringBuilder();

        int pointer = 0;

        for (String s : arrayFilteredFromFile) {
            if (numberDictionary.containsKey(s)) {
                newStr.append(numberDictionary.get(s));
            } else {
                newStr.append("?");
            }

            pointer++;

            if (pointer % 9 == 0) {
                creditCardNumber.add(String.valueOf(newStr));
                newStr.delete(0, 10);
            }
        }

        return creditCardNumber;

    }

    @PostMapping("/uploadFile")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {

        fileSystemStorageService.store(file);

        Archivo archivo = new Archivo();
        archivo.setNombre(name);
        archivo.setFecha(LocalDateTime.now());
        archivoRepository.save(archivo);

        return name;
    }
}