package br.com.ilegra.challenge.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleItem {

    String id;

    Integer quantity;
    Double price;

    SaleItem(final String value) {
        final String[] values = value.split("-");
        id = values[0];
        quantity = Integer.valueOf(values[1]);
        price = new Double(values[2].replaceAll("]", ""));
    }

}