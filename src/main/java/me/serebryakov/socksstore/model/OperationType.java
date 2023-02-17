package me.serebryakov.socksstore.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OperationType {
    WRITE_OFF("Списание"),
    RECEIVING("Приемка"),
    ISSUANCE("Выдача");

    private final String name;
}
