package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.service.CarService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarControllerTest {

    @Mock
    private CarService carService;

    @InjectMocks
    private CarController controller;

    @Test
    void createCarPage_shouldReturnFormPage() {
        Model model = new ExtendedModelMap();

        String view = controller.createCarPage(model);

        assertEquals("createCar", view);
        assertNotNull(model.getAttribute("car"));
    }

    @Test
    void createCarPost_shouldDelegateAndRedirect() {
        Car car = new Car();
        car.setCarName("Brio");

        String view = controller.createCarPost(car, new ExtendedModelMap());

        assertEquals("redirect:listCar", view);
        verify(carService).create(car);
    }

    @Test
    void carListPage_shouldReturnListPage() {
        Car car = new Car();
        car.setCarId("car-1");
        when(carService.findAll()).thenReturn(List.of(car));
        Model model = new ExtendedModelMap();

        String view = controller.carListPage(model);

        assertEquals("carList", view);
        assertEquals(1, ((List<?>) model.getAttribute("cars")).size());
        verify(carService).findAll();
    }

    @Test
    void editCarPage_shouldAddSelectedCar() {
        Car car = new Car();
        car.setCarId("car-1");
        when(carService.findById("car-1")).thenReturn(car);
        Model model = new ExtendedModelMap();

        String view = controller.editCarPage("car-1", model);

        assertEquals("editCar", view);
        assertEquals(car, model.getAttribute("car"));
        verify(carService).findById("car-1");
    }

    @Test
    void editCarPost_shouldDelegateAndRedirect() {
        Car car = new Car();
        car.setCarId("car-1");

        String view = controller.editCarPost(car, new ExtendedModelMap());

        assertEquals("redirect:listCar", view);
        verify(carService).update("car-1", car);
    }

    @Test
    void deleteCar_shouldDelegateAndRedirect() {
        String view = controller.deleteCar("car-1");

        assertEquals("redirect:listCar", view);
        verify(carService).deleteCarById("car-1");
    }
}
