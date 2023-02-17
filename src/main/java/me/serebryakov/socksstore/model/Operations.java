package me.serebryakov.socksstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Operations {
    OperationType type;
    String dateTime;
    int quantity;
    Size size;
    int cottonPart;
    Colors color;
}
