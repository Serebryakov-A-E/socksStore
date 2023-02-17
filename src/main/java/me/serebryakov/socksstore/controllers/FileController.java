package me.serebryakov.socksstore.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import me.serebryakov.socksstore.services.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RestController
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;

    @Value("${path.to.data.file}")
    private String pathToDataFile;

    @Value("${name.of.data.file}")
    private String nameOfDataFile;

    @Value("${name.of.operations.file}")
    private String nameOfOperationFile;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/download")
    @Operation(summary = "Получить файл с информацией по всем носкам")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно"
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Файла не существует или он пустой"
            )
    }
    )
    public ResponseEntity<InputStreamResource> getSocksFile() throws FileNotFoundException {
        File file = fileService.getDataFile(nameOfDataFile);

        if (file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .contentLength(file.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"SocksStorage.json\"")
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить файл с носками")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл успешно загружен"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка на стороне сервера"
            )
    }
    )
    public ResponseEntity<Void> uploadRecipesDataFile(@RequestParam MultipartFile file) {
        if (fileService.uploadDataFile(file, nameOfDataFile)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/Operations/download")
    @Operation(summary = "Получить файл с информацией по всем операциям")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно"
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Файла не существует или он пустой"
            )
    }
    )
    public ResponseEntity<InputStreamResource> getOperationsFile() throws FileNotFoundException {
        File file = fileService.getDataFile(nameOfOperationFile);

        if (file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .contentLength(file.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Operations.json\"")
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "/operations/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить файл с операциями")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл успешно загружен"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка на стороне сервера"
            )
    }
    )
    public ResponseEntity<Void> uploadOperationsDataFile(@RequestParam MultipartFile file) {
        if (fileService.uploadDataFile(file, nameOfOperationFile)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
