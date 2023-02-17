package me.serebryakov.socksstore.services;

import me.serebryakov.socksstore.model.OperationType;
import me.serebryakov.socksstore.model.Socks;

import java.io.IOException;
import java.nio.file.Path;

public interface OperationService {
    void addOperation(Socks socks, OperationType operationType);

    void saveFile();

    Path createOperationsFile() throws IOException;

    void readFromFile();
}
