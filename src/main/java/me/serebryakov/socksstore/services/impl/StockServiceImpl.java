package me.serebryakov.socksstore.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.serebryakov.socksstore.model.Colors;
import me.serebryakov.socksstore.model.OperationType;
import me.serebryakov.socksstore.model.Size;
import me.serebryakov.socksstore.model.Socks;
import me.serebryakov.socksstore.services.FileService;
import me.serebryakov.socksstore.services.OperationService;
import me.serebryakov.socksstore.services.StockService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {
    private List<Socks> socksStorage = new ArrayList<>();

    private final FileService fileService;
    private final OperationService operationService;

    public StockServiceImpl(FileService fileService, OperationService operationService) {
        this.fileService = fileService;
        this.operationService = operationService;
    }

    @PostConstruct
    public void init() {
        readFromFile();
    }

    @Value("${name.of.data.file}")
    private String dataFileName;

    @Override
    //приемка
    public boolean add(Socks socks) {
        if (socksStorage.contains(socks)) {
            int index = socksStorage.indexOf(socks);
            socksStorage.get(index).addQuantity(socks.getQuantity());
        } else {
            socksStorage.add(socks);
        }
        operationService.addOperation(socks, OperationType.RECEIVING);
        saveFile();
        return true;
    }

    @Override
    public int getAmount(Colors color, Size size, int cottonMin, int cottonMax) {
        int sum = 0;
        for (Socks socks : socksStorage) {
            if (socks.getColor() == color && socks.getSize() == size && socks.getCottonPart() <= cottonMax && socks.getCottonPart() >= cottonMin) {
                sum += socks.getQuantity();
            }
        }
        return sum;
    }

    @Override
    public int getAmount(Colors color, Size size, int cottonMin) {
        int sum = 0;
        for (Socks socks : socksStorage) {
            if (socks.getColor() == color && socks.getSize() == size && socks.getCottonPart() >= cottonMin) {
                sum += socks.getQuantity();
            }
        }
        return sum;
    }

    @Override
    public int getAmount(Colors color, int cottonMax, Size size) {
        int sum = 0;
        for (Socks socks : socksStorage) {
            if (socks.getColor() == color && socks.getSize() == size && socks.getCottonPart() <= cottonMax) {
                sum += socks.getQuantity();
            }
        }
        return sum;
    }

    @Override
    public List<Socks> get(Colors color, Size size, int cottonMin, int cottonMax) {
        List<Socks> list = new ArrayList<>();
        socksStorage.forEach(socks -> {
            if (socks.getColor() == color && socks.getSize() == size && socks.getCottonPart() <= cottonMax && socks.getCottonPart() >= cottonMin) {
               list.add(socks);
            }
        });
        return list;
    }

    @Override
    //выдача
    public Socks getFromStock(Socks socks) {
        if (socksStorage.contains(socks)) {
            int index = socksStorage.indexOf(socks);
            if (socksStorage.get(index).getQuantity() >= socks.getQuantity()) {
                socksStorage.get(index).subQuantity((socks.getQuantity()));
                operationService.addOperation(socks, OperationType.ISSUANCE);
                saveFile();
                return socksStorage.get(index);
            }
        }
        return null;
    }

    @Override
    //списание
    public boolean deleteFromStock(Socks socks) {
        if (socksStorage.contains(socks)) {
            int index = socksStorage.indexOf(socks);
            if (socksStorage.get(index).getQuantity() >= socks.getQuantity()) {
                socksStorage.get(index).subQuantity((socks.getQuantity()));
                operationService.addOperation(socks, OperationType.WRITE_OFF);
                saveFile();
                return true;
            }
        }
        return false;
    }

    private void saveFile() {
        try {
            String json = new ObjectMapper().writeValueAsString(socksStorage);
            fileService.saveToFile(json, dataFileName);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFromFile() {
        try {
            String json = fileService.reedFromFile(dataFileName);
            if (StringUtils.isBlank(json)) {
                socksStorage = new ArrayList<>();
            } else {
                socksStorage = new ObjectMapper().readValue(json, new TypeReference<List<Socks>>() {
                });
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path createCurrentRecipes() throws IOException {
        Path path = fileService.createTempFile("current");
        for (Socks socks : socksStorage) {
            try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
                writer.append("Размер: " + socks.getSize() + ". Цвет: " + socks.getColor() + ". Процент хлопка: "
                        + socks.getCottonPart() + ". Количество: " + socks.getQuantity());
                writer.append("\n");
            }
        }
        return path;
    }
}
