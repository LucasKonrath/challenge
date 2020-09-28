package br.com.ilegra.challenge.database;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class MemoryDatabase {

    public Integer salesmenQuantity = 0;
    public Integer customerQuantity = 0;

    public Map<Integer, Double> salesMap = new HashMap<>();

    public Map<String, Double> salesmen = new HashMap<>();

    public void tearDown() {
        setSalesmenQuantity(0);
        setCustomerQuantity(0);
        setSalesMap(new HashMap<>());
        setSalesmen(new HashMap<>());
    }

}
