package br.com.ilegra.challenge.batch.writer;

import static java.util.Optional.ofNullable;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.ilegra.challenge.database.MemoryDatabase;
import br.com.ilegra.challenge.domain.Customer;
import br.com.ilegra.challenge.domain.Sale;
import br.com.ilegra.challenge.domain.SaleItem;
import br.com.ilegra.challenge.domain.Salesman;

@Component
public class MemoryItemWriter<T> implements ItemWriter<T> {

    @Autowired
    private MemoryDatabase memoryDatabase;

    @Override
    public void write(final List<? extends T> items) throws Exception {

        for (final T item : items) {

            if (item instanceof Customer) {
                memoryDatabase.customerQuantity++;
            }

            if (item instanceof Salesman) {
                memoryDatabase.salesmenQuantity++;
            }

            if (item instanceof Sale) {

                final Sale currentSale = (Sale) item;

                final List<SaleItem> saleItems = currentSale.getItems();

                final Integer saleId = currentSale.getSaleId();
                Double value = 0.0;

                for (final SaleItem saleItem : saleItems) {
                    value += saleItem.getPrice() * saleItem.getQuantity();
                }

                memoryDatabase.getSalesMap()
                    .put(saleId, value);

                final String salesman = currentSale.getSalesmanName();

                final Double historicSales = ofNullable(memoryDatabase.getSalesmen().get(salesman))
                    .orElse(0.0);

                memoryDatabase.getSalesmen().put(salesman, value + historicSales);
            }

        }
    }
}