import customer.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;
import shopping.BuyException;
import shopping.Cart;
import shopping.ShoppingService;
import shopping.ShoppingServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingServiceTest {

    private final ProductDao productDao = Mockito.mock(ProductDao.class);
    private final ShoppingService shoppingService = new ShoppingServiceImpl(productDao);

    @Test
    @DisplayName(
            """
            Тест проверяет, что при запросе корзины покупателя возвращается свежая версия корзины.
            Стоит учесть, что такое поведение может быть неоднозначным если пользователь добавил товар через ПК,
            ожидалось бы, что его корзина сохранится, но текущая реализация возвращает новую корзину при каждом запросе,
            что может вызвать несогласованность данных.
            """
    )
    void getCartTest() {
        Customer customer = new Customer(9L, "89829996565");

        Cart firstGetCart = shoppingService.getCart(customer);
        Cart secondGetCard = shoppingService.getCart(customer);

        assertEquals(firstGetCart, secondGetCard);
        assertEquals(firstGetCart.getProducts().size(), secondGetCard.getProducts().size());

        Product milk = new Product();
        milk.setName("Milk");
        milk.addCount(10);
        firstGetCart.add(milk, 1);
        Cart thirdGetCard = shoppingService.getCart(customer);

        assertEquals(firstGetCart, thirdGetCard);
        assertEquals(firstGetCart.getProducts().get(milk), secondGetCard.getProducts().get(milk));
    }

    @Test
    @DisplayName(
            """
            Тест на проверку совершения покупки
            Непустая корзина - покупка происходит, количество продуктов уменьшается
                1. Непустая корзина - покупка происходит, количество продуктов уменьшается
                2. Пустая корзина - покупка не происходит
                3. Товаров нет - выбрасывается исключение
            """
    )
    void buyProductTest() throws BuyException {
        Customer customer = new Customer(9L, "89829996565");
        Cart cart = shoppingService.getCart(customer);
        assertFalse(shoppingService.buy(cart));

        Product chocolate = new Product();
        chocolate.setName("milka");
        chocolate.addCount(10);
        cart.add(chocolate, 1);
        assertTrue(shoppingService.buy(cart));

        Mockito.verify(productDao).save(
                Mockito.argThat((Product product) -> product.getName().equals("milka") && product.getCount() == 9));
        Mockito.verify(productDao).save(chocolate);

        Customer fastestHandsInTheWildWest = new Customer(10L, "89829991212");
        Cart fastestHandsCart = shoppingService.getCart(fastestHandsInTheWildWest);
        fastestHandsCart.add(chocolate, 5);
        cart.edit(chocolate, 5);
        shoppingService.buy(fastestHandsCart);

        Exception exception = assertThrows(BuyException.class, () -> shoppingService.buy(cart));
        assertEquals("В наличии нет необходимого количества товара milka", exception.getMessage());
    }

    @Test
    @DisplayName("Излишен, так как метод, отвечающий за возврат всех продуктов лежит на плечах класса ProductDao")
    void getAllProductsTest() {}

    @Test
    @DisplayName("Излишен, так как метод, отвечающий за поиск продуктов по имени лежит на плечах класса ProductDao")
    void getProductByNameTest() {}
}
