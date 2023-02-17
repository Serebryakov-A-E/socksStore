package me.serebryakov.socksstore.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum Size {
    size36(36),
    size37(37),
    size38(38),
    size39(39),
    size40(40),
    size41(41),
    size42(42),
    size43(43),
    size44(44),
    size45(45);

    private int size;

    public int getSize() {
        return size;
    }
}
