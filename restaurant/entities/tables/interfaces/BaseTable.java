package restaurant.entities.tables.interfaces;

import restaurant.entities.drinks.interfaces.Beverages;
import restaurant.entities.healthyFoods.interfaces.HealthyFood;

import java.util.ArrayList;
import java.util.Collection;

import static restaurant.common.ExceptionMessages.*;

public abstract class BaseTable implements Table {
    private Collection<HealthyFood> healthyFood;
    private Collection<Beverages> beverages;
    private int number;
    private int size;
    private int numberOfPeople;
    private double pricePerPerson;
    private boolean isReservedTable;
    private double allPeople;


    protected BaseTable(int number, int size, double pricePerPerson) {
        this.healthyFood = new ArrayList<>();
        this.beverages = new ArrayList<>();
        this.number = number;
        this.setSize(size);
        this.numberOfPeople = 0;
        this.pricePerPerson = pricePerPerson;
        this.allPeople = 0;


    }

    protected void setSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException(INVALID_TABLE_SIZE);
        }
        this.size = size;
    }


    @Override
    public int getTableNumber() {
        return this.number;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public int numberOfPeople() {
        return this.numberOfPeople;
    }

    @Override
    public double pricePerPerson() {
        return this.pricePerPerson;
    }

    @Override
    public boolean isReservedTable() {
        return this.isReservedTable;
    }

    @Override
    public double allPeople() {
        return this.allPeople;
    }

    @Override
    public void reserve(int numberOfPeople) {
        this.isReservedTable = true;
        this.allPeople = numberOfPeople * this.pricePerPerson;
        this.numberOfPeople = numberOfPeople;
    }

    @Override
    public void orderHealthy(HealthyFood food) {
        this.healthyFood.add(food);

    }

    @Override
    public void orderBeverages(Beverages beverages) {
        this.beverages.add(beverages);
    }

    @Override
    public double bill() {
        double sumFood = healthyFood.stream().mapToDouble(HealthyFood::getPrice).sum();
        double sumBeverages = beverages.stream().mapToDouble(Beverages::getPrice).sum();
        return sumFood + sumBeverages + allPeople();
    }

    @Override
    public void clear() {
        this.healthyFood.clear();
        this.beverages.clear();
        this.isReservedTable = false;
        this.allPeople = 0;
        this.numberOfPeople = 0;
    }

    @Override
    public String tableInformation() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Table - %d", this.number)).append(System.lineSeparator());
        builder.append(String.format("Size - %d", this.size)).append(System.lineSeparator());
        builder.append(String.format("Type - %s", this.getClass().getSimpleName())).append(System.lineSeparator());
        builder.append(String.format("All price - %.2f", this.pricePerPerson));

        return builder.toString();
    }
}
