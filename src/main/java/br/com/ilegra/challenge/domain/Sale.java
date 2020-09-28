package br.com.ilegra.challenge.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class Sale {

    String id;

    Integer saleId;
    String saleItems;
    String salesmanName;

    public List<SaleItem> getItems() {
        return Arrays.stream(
            saleItems.split(",")
        ).map(it -> new SaleItem(it))
            .collect(Collectors.toList());

    }
}