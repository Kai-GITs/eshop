package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void create_shouldDelegateToRepositoryAndReturnCar() {
        Car car = buildCar("car-1", "Brio", "White", 1);

        Car created = carService.create(car);

        verify(carRepository).create(car);
        assertSame(car, created);
    }

    @Test
    void findAll_shouldConvertIteratorToList() {
        Car first = buildCar("car-1", "Brio", "White", 1);
        Car second = buildCar("car-2", "Jazz", "Blue", 3);
        when(carRepository.findAll()).thenReturn(List.of(first, second).iterator());

        List<Car> cars = carService.findAll();

        assertEquals(2, cars.size());
        assertEquals("car-1", cars.get(0).getCarId());
        assertEquals("car-2", cars.get(1).getCarId());
        verify(carRepository).findAll();
    }

    @Test
    void findById_shouldReturnRepositoryResult() {
        Car car = buildCar("car-1", "Brio", "White", 1);
        when(carRepository.findById("car-1")).thenReturn(car);

        Car found = carService.findById("car-1");

        assertSame(car, found);
        verify(carRepository).findById("car-1");
    }

    @Test
    void update_shouldDelegateToRepository() {
        Car car = buildCar("car-1", "Civic", "Black", 4);

        carService.update("car-1", car);

        verify(carRepository).update("car-1", car);
    }

    @Test
    void deleteCarById_shouldDelegateToRepository() {
        carService.deleteCarById("car-1");

        verify(carRepository).delete("car-1");
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
