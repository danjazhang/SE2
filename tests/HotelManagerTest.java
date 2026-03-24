import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HotelManagerTest {
    @Test
void testMeerdereLayoutsOpslaan() {
    HotelManager manager = new HotelManager();
    Layout layout1 = new Layout(6, 8);
    Layout layout2 = new Layout(4, 5);

    manager.addLayout("layout1", layout1);
    manager.addLayout("layout2", layout2);

    assertEquals(2, manager.getAllLayoutIds().size());
}

@Test
void testGetLayout() {
    HotelManager manager = new HotelManager();
    Layout layout = new Layout(6, 8);

    int id = manager.addLayout("test", layout);

    assertEquals(layout, manager.getLayout(id));
}

@Test
void testMeerdereHotelsOpslaan() {
    HotelManager manager = new HotelManager();
    Hotel hotel1 = new Hotel();
    Hotel hotel2 = new Hotel();

    manager.loadHotel(1, hotel1);
    manager.loadHotel(2, hotel2);

    assertEquals(hotel1, manager.getHotel(1));
    assertEquals(hotel2, manager.getHotel(2));
}

}
