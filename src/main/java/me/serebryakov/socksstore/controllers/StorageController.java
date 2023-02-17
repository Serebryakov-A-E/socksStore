package me.serebryakov.socksstore.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.serebryakov.socksstore.model.Colors;
import me.serebryakov.socksstore.model.Size;
import me.serebryakov.socksstore.model.Socks;
import me.serebryakov.socksstore.services.impl.StockServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@Tag(name = "Учёт товаров", description = "Операции по учёту товара на складе интернет-магазина носков")
@RequestMapping("/storage")
public class StorageController {

    private final StockServiceImpl stockService;

    public StorageController(StockServiceImpl stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    @Operation(summary = "Добавление носков на склад")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "удалось добавить приход"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "параметры запроса отсутствуют или имеют некорректный формат"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "произошла ошибка, не зависящая от вызывающей стороны"
            )
    })
    public ResponseEntity<Void> addSocks(@RequestBody Socks socks) {
        stockService.add(socks);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getSocksTxt")
    @Operation(summary = "Получение списка носков в txt")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно"
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Носков на складе нет"
            )
    })
    public ResponseEntity<Object> getCurrentStock() {
        try {
            Path path = stockService.createCurrentRecipes();
            if (Files.size(path) == 0) {
                return ResponseEntity.noContent().build();
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(path.toFile()));
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"socks.txt\"")
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping()
    @Operation(summary = "Получить носки по запросу")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "запрос выполнен, результат в теле ответа в виде строкового представления целого числа"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "параметры запроса отсутствуют или имеют некорректный формат"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "произошла ошибка, не зависящая от вызывающей стороны"
            )
    })
    public ResponseEntity<Integer> getSocks(@RequestParam Colors color, @RequestParam Size size, @RequestParam(value = "cottonMin", required = false) Integer cottonMin, @RequestParam(value = "cottonMax", required = false) Integer cottonMax) {
        int result;
        if (cottonMax == null) {
            result = stockService.getAmount(color, size, cottonMin);
        } else if (cottonMin == null) {
            result = stockService.getAmount(color,cottonMax, size);
        } else {
            result = stockService.getAmount(color, size, cottonMin, cottonMax);
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/get")
    @Operation(summary = "Забрать носки со склада")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "удалось произвести отпуск носков со склада"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "товара нет на складе в нужном количестве или параметры запроса имеют некорректный формат"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "произошла ошибка, не зависящая от вызывающей стороны"
            )
    })
    public  ResponseEntity<Socks> getFrom(@RequestBody Socks socks) {
        Socks socks1 = stockService.getFromStock(socks);
        if (ObjectUtils.isNotEmpty(socks1)) {
            return ResponseEntity.ok(socks1);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping
    @Operation(summary = "Списать испорченные носки со склада")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "запрос выполнен, товар списан со склада"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "параметры запроса отсутствуют или имеют некорректный формат"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "товара соответствующего запросу не найдено"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "произошла ошибка, не зависящая от вызывающей стороны"
            )
    })
    public ResponseEntity<Void> deleteSocks(Socks socks) {
        if (stockService.deleteFromStock(socks)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
