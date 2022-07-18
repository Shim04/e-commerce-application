package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private static final String USERNAME = "tester";
    private static final String PASSWORD = "testPassword";
    private static final String DRESS = "Knit Midi Dress";

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void testAddAndRemoveHappyPath() {
        Item dress = new Item(1L, DRESS, new BigDecimal(88.8));
        Cart cart = new Cart();
        User user = new User(1L, USERNAME, PASSWORD, cart);
        ModifyCartRequest modifyCartRequest = createCartRequest(2);

        // Add to cart
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(dress));
        ResponseEntity<Cart> responseAdd = cartController.addToCart(modifyCartRequest);
        assertNotNull(responseAdd);
        assertEquals(200, responseAdd.getStatusCodeValue());
        Cart returnedCart = responseAdd.getBody();
        assertEquals(Arrays.asList(dress, dress), returnedCart.getItems());

        // Remove from cart
        modifyCartRequest = createCartRequest(1);
        ResponseEntity<Cart> responseRemove = cartController.removeFromCart(modifyCartRequest);
        assertNotNull(responseRemove);
        assertEquals(200, responseRemove.getStatusCodeValue());
        returnedCart = responseRemove.getBody();
        assertEquals(Arrays.asList(dress), returnedCart.getItems());
    }

    @Test
    public void testAddFail() {
        // User not found
        ModifyCartRequest modifyCartRequest = createCartRequest(2);
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);
        ResponseEntity<Cart> responseUserNotFound = cartController.addToCart(modifyCartRequest);
        assertNull(responseUserNotFound.getBody());
        assertEquals(404, responseUserNotFound.getStatusCodeValue());

        // Item not found
        Cart cart = new Cart();
        User user = new User(1L, USERNAME, PASSWORD, cart);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Cart> responseItemNotFound = cartController.addToCart(modifyCartRequest);
        assertNull(responseItemNotFound.getBody());
        assertEquals(404, responseItemNotFound.getStatusCodeValue());
    }

    @Test
    public void testRemoveFail() {
        // User not found
        ModifyCartRequest modifyCartRequest = createCartRequest(2);
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);
        ResponseEntity<Cart> responseUserNotFound = cartController.removeFromCart(modifyCartRequest);
        assertNull(responseUserNotFound.getBody());
        assertEquals(404, responseUserNotFound.getStatusCodeValue());

        // Item not found
        Cart cart = new Cart();
        User user = new User(1L, USERNAME, PASSWORD, cart);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Cart> responseItemNotFound = cartController.removeFromCart(modifyCartRequest);
        assertNull(responseItemNotFound.getBody());
        assertEquals(404, responseItemNotFound.getStatusCodeValue());
    }

    private ModifyCartRequest createCartRequest(int quantity) {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(quantity);
        modifyCartRequest.setUsername(USERNAME);
        return modifyCartRequest;
    }
}
