package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private static final String USERNAME = "tester";
    private static final String PASSWORD = "testPassword";
    private static final String DRESS = "Knit Midi Dress";
    private static final String SHOE = "Hydro Slide";
    private static final String SHIRT = "Sail Shirt";

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void testSubmitHappyPath() {
        Cart cart = createCart();
        User user = new User(1L, USERNAME, PASSWORD, cart);
        cart.setUser(user);

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit(USERNAME);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder userOrder = response.getBody();
        assertEquals(cart.getItems(), userOrder.getItems());
        assertEquals(USERNAME, userOrder.getUser().getUsername());
        assertEquals(cart.getTotal(), userOrder.getTotal());
    }

    @Test
    public void testSubmitFail() {
        ResponseEntity<UserOrder> response = orderController.submit(USERNAME);
        assertNull(response.getBody());
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testGetOrderHappyPath() {
        Cart cart = createCart();
        User user = new User(1L, USERNAME, PASSWORD, cart);
        cart.setUser(user);
        UserOrder userOrder = UserOrder.createFromCart(cart);
        List<UserOrder> userOrders = Arrays.asList(userOrder);

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(userOrders);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(USERNAME);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userOrders, response.getBody());
    }

    @Test
    public void testGetOrderNotFound() {
        ResponseEntity<UserOrder> response = orderController.submit(USERNAME);
        assertNull(response.getBody());
        assertEquals(404, response.getStatusCodeValue());
    }

    private Cart createCart() {
        Item dress = new Item(1L, DRESS, new BigDecimal(88.8));
        Item shoe = new Item(2L, SHOE, new BigDecimal(77.7));
        Item shirt = new Item(3L, SHIRT, new BigDecimal(66.6));
        List<Item> items = Arrays.asList(dress, shoe, shirt);
        BigDecimal total = new BigDecimal(0);
        items.forEach(item -> total.add(item.getPrice()));
        Cart cart = new Cart(items, total);
        return cart;
    }
}
