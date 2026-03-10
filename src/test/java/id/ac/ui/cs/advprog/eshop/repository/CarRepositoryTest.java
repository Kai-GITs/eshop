package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CarRepositoryTest {
    private CarRepository carRepository;

    @BeforeEach
    void setUp() {
        carRepository = new CarRepository();
    }

    @Test
    void create_whenIdNull_shouldGenerateId() {
        Car car = buildCar(null, "Avanza", "Black", 2);

        Car created = carRepository.create(car);

        assertNotNull(created.getCarId());
        assertEquals("Avanza", created.getCarName());
    }

    @Test
    void create_whenIdExists_shouldKeepId() {
        Car car = buildCar("car-1", "Brio", "White", 1);

        Car created = carRepository.create(car);

        assertEquals("car-1", created.getCarId());
    }

    @Test
    void findAll_shouldReturnAllCreatedCars() {
        carRepository.create(buildCar("car-1", "Brio", "White", 1));
        carRepository.create(buildCar("car-2", "Jazz", "Blue", 3));

        Iterator<Car> iterator = carRepository.findAll();
        List<Car> result = new ArrayList<>();
        iterator.forEachRemaining(result::add);

        assertEquals(2, result.size());
        assertEquals("car-1", result.get(0).getCarId());
        assertEquals("car-2", result.get(1).getCarId());
    }

    @Test
    void findById_whenFound_shouldReturnCar() {
        Car car = carRepository.create(buildCar("car-1", "Brio", "White", 1));

        Car found = carRepository.findById("car-1");

        assertEquals(car.getCarId(), found.getCarId());
        assertEquals("Brio", found.getCarName());
    }

    @Test
    void findById_whenNotFound_shouldReturnNull() {
        assertNull(carRepository.findById("unknown"));
    }

    @Test
    void update_whenFound_shouldUpdateFields() {
        carRepository.create(buildCar("car-1", "Brio", "White", 1));
        Car updatedData = buildCar("car-1", "Civic", "Black", 4);

        Car updated = carRepository.update("car-1", updatedData);

        assertNotNull(updated);
        assertEquals("Civic", updated.getCarName());
        assertEquals("Black", updated.getCarColor());
        assertEquals(4, updated.getCarQuantity());
    }

    @Test
    void update_whenNotFound_shouldReturnNull() {
        Car updated = carRepository.update("unknown", buildCar("x", "Civic", "Black", 4));
        assertNull(updated);
    }

    @Test
    void delete_shouldRemoveCarById() {
        carRepository.create(buildCar("car-1", "Brio", "White", 1));
        carRepository.create(buildCar("car-2", "Jazz", "Blue", 3));

        carRepository.delete("car-1");

        assertNull(carRepository.findById("car-1"));
        assertNotNull(carRepository.findById("car-2"));
        List<Car> result = new ArrayList<>();
        carRepository.findAll().forEachRemaining(result::add);
        assertTrue(result.size() == 1);
    }

    private Car buildCar(String id, String name, String color, int quantity) {
        Car car = new Car();
        car.setCarId(id);
        car.setCarName(name);
        car.setCarColor(color);
        car.setCarQuantity(quantity);
        return car;
    }
}
