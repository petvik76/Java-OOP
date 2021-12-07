package restaurant.core;

import restaurant.core.interfaces.Controller;
import restaurant.entities.drinks.interfaces.Fresh;
import restaurant.entities.drinks.interfaces.Smoothie;
import restaurant.entities.healthyFoods.interfaces.HealthyFood;
import restaurant.entities.drinks.interfaces.Beverages;
import restaurant.entities.healthyFoods.interfaces.Salad;
import restaurant.entities.healthyFoods.interfaces.VeganBiscuits;
import restaurant.entities.tables.interfaces.InGarden;
import restaurant.entities.tables.interfaces.Indoors;
import restaurant.entities.tables.interfaces.Table;
import restaurant.repositories.interfaces.*;

import static restaurant.common.ExceptionMessages.*;
import static restaurant.common.OutputMessages.*;

public class ControllerImpl implements Controller {
    private HealthFoodRepository<HealthyFood> healthFoodRepository;
    private BeverageRepository<Beverages> beverageRepository;
    private TableRepository<Table> tableRepository;
    private double totalMoney;


    public ControllerImpl(HealthFoodRepository<HealthyFood> healthFoodRepository, BeverageRepository<Beverages> beverageRepository, TableRepository<Table> tableRepository) {
        this.healthFoodRepository = new HealthFoodRepositoryImpl();
        this.beverageRepository = new BeverageRepositoryImpl();
        this.tableRepository = new TableRepositoryImpl();
        this.totalMoney = 0;
    }

    @Override
    public String addHealthyFood(String type, double price, String name) {

        HealthyFood food = null;
        switch (type) {
            case "Salad":
                food = new Salad(name, price);
                break;
            case "VeganBiscuits":
                food = new VeganBiscuits(name, price);
                break;
        }
        if (this.healthFoodRepository.foodByName(name) != null) {
            throw new IllegalArgumentException(String.format(FOOD_EXIST, name));
        }
        this.healthFoodRepository.add(food);
        return String.format(FOOD_ADDED, name);
    }

    @Override
    public String addBeverage(String type, int counter, String brand, String name) {

        Beverages beverage = null;
        switch (type) {
            case "Smoothie":
                beverage = new Smoothie(name, counter, brand);
                break;
            case "Fresh":
                beverage = new Fresh(name, counter, brand);
                break;
        }
        if (this.beverageRepository.beverageByName(name, brand) != null) {
            throw new IllegalArgumentException(String.format(BEVERAGE_EXIST, name));

        }
        this.beverageRepository.add(beverage);
        return String.format(BEVERAGE_ADDED, type, brand);
    }

    @Override
    public String addTable(String type, int tableNumber, int capacity) {

        Table table = null;
        switch (type) {
            case "InGarden":
                table = new InGarden(tableNumber, capacity);
                break;
            case "Indoors":
                table = new Indoors(tableNumber, capacity);
                break;
        }
        if (this.tableRepository.byNumber(tableNumber) != null) {
            throw new IllegalArgumentException(String.format(TABLE_IS_ALREADY_ADDED, tableNumber));
        }
        this.tableRepository.add(table);
        return String.format(TABLE_ADDED, tableNumber);
    }

    @Override
    public String reserve(int numberOfPeople) {
        Table freeTable = this.tableRepository.getAllEntities().stream().filter(t->!t.isReservedTable() && t.getSize() >= numberOfPeople)
                .findFirst().orElse(null);
        if (freeTable == null) {
            return String.format(RESERVATION_NOT_POSSIBLE, numberOfPeople);
        }
        freeTable.reserve(numberOfPeople);
        return String.format(TABLE_RESERVED, freeTable.getTableNumber(), numberOfPeople);
    }

    @Override
    public String orderHealthyFood(int tableNumber, String healthyFoodName) {
       Table table = tableRepository.byNumber(tableNumber);
       if (table == null) {
           return String.format(WRONG_TABLE_NUMBER, tableNumber);
       }
       HealthyFood food = healthFoodRepository.foodByName(healthyFoodName);
       if (food == null) {
           return String.format(NONE_EXISTENT_FOOD, healthyFoodName);
       }
        table.orderHealthy(food);
       return String.format(FOOD_ORDER_SUCCESSFUL, healthyFoodName, tableNumber);
    }

    @Override
    public String orderBeverage(int tableNumber, String name, String brand) {
        Table table = tableRepository.byNumber(tableNumber);
        if (table == null) {
            return String.format(WRONG_TABLE_NUMBER, tableNumber);
        }
        Beverages beverage = beverageRepository.beverageByName(name, brand);

        if (beverage == null) {
            return String.format(NON_EXISTENT_DRINK, name, brand);
        }
        table.orderBeverages(beverage);
        return String.format(BEVERAGE_ORDER_SUCCESSFUL, name, tableNumber);
    }

    @Override
    public String closedBill(int tableNumber) {
        Table table = tableRepository.byNumber(tableNumber);
        double bill = table.bill();
        this.totalMoney += bill;
        table.clear();
        return String.format(BILL, tableNumber, bill);
    }


    @Override
    public String totalMoney() {
        return String.format(TOTAL_MONEY, totalMoney);
    }
}
