package br.com.ilegra.challenge.domain;

import lombok.Data;

@Data
public class Customer {
 
    String id;

    String cnpj;
    String name;
    String businessArea;

}