package me.serebryakov.socksstore.services;

import me.serebryakov.socksstore.model.Colors;
import me.serebryakov.socksstore.model.Size;
import me.serebryakov.socksstore.model.Socks;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface StockService {
    boolean add(Socks socks);

    int getAmount(Colors color, Size size, int cottonMin, int cottonMax);

    int getAmount(Colors color, Size size, int cottonMin);

    int getAmount(Colors color, int cottonMax, Size size);

    List<Socks> get(Colors color, Size size, int cottonMin, int cottonMax);

    Socks getFromStock(Socks socks);

    boolean deleteFromStock(Socks socks);

    Path createCurrentRecipes() throws IOException;
}
