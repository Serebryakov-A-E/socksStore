package me.serebryakov.socksstore.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Colors {
    WHITE("Белый"),
    BLACK("Черный"),
    YELLOW("Желтый"),
    GREEN("Зеленый"),
    BROWN("Коричневый"),
    RED("Красный"),
    BLUE("Синий");

    private final String name;
}
