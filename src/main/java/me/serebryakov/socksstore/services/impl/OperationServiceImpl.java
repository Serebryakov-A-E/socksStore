package me.serebryakov.socksstore.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.serebryakov.socksstore.model.OperationType;
import me.serebryakov.socksstore.model.Operations;
import me.serebryakov.socksstore.model.Socks;
import me.serebryakov.socksstore.services.FileService;
import me.serebryakov.socksstore.services.OperationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OperationServiceImpl implements OperationService {
    private final FileService fileService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");

    private List<Operations> operations = new ArrayList<>();

    @Value("${path.to.data.file}")
    private String dataFilePath;

    @Value("${name.of.operations.file}")
    private String operationFileName;

    public OperationServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @PostConstruct
    public void init() {
        readFromFile();
    }

    @Override
    public void addOperation(Socks socks, OperationType operationType) {
        operations.add(new Operations(operationType, LocalDateTime.now().toString(), socks.getQuantity(), socks.getSize(), socks.getCottonPart(), socks.getColor()));
        saveFile();
    }

    @Override
    public void saveFile() {
        try {
            String json = new ObjectMapper().writeValueAsString(operations);
            fileService.saveToFile(json, operationFileName);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path createOperationsFile() throws IOException {
        Path path = fileService.createTempFile("currentState");
        int i = 1;
        for (Operations operation : operations) {
            try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
                writer.append("Операция №" + i++);
                writer.append("\n");
                writer.append("Тип операции: " + operation.getType().name() + ".\n");
                writer.append("Дата: " + LocalDateTime.parse(operation.getDateTime()).format(formatter) + ".\n");
                writer.append("Размер: " + operation.getSize() + ".\n");
                writer.append("Цвет: " + operation.getColor().name() + ".\n");
                writer.append("Процент хлопка: " + operation.getCottonPart() + "%.\n");
                writer.append("Количество: " + operation.getQuantity() + ".\n");
                writer.append("\n");
            }
        }
        return path;
    }

    @Override
    public void readFromFile() {
        try {
            String json = fileService.reedFromFile(operationFileName);
            if (StringUtils.isBlank(json)) {
                operations = new ArrayList<>();
            } else {
                operations = new ObjectMapper().readValue(json, new TypeReference<List<Operations>>() {
                });
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
