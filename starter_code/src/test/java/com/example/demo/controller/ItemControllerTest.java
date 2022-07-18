package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private static final String DRESS = "Knit Midi Dress";
    private static final String SHOE = "Hydro Slide";
    private static final String SHIRT = "Sail Shirt";

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testGetAllItems() {
        List<Item> items = createItemList();
        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(items, response.getBody());
    }

    @Test
    public void testFindItemHappyPath() {
        Item dress = new Item(1L, DRESS, new BigDecimal(88.8));
        Item beautifulDress = new Item(2L, DRESS, new BigDecimal(111.11));
        // Find by id
        when(itemRepository.findById(1L)).thenReturn(Optional.of(dress));
        ResponseEntity<Item> responseById = itemController.getItemById(1L);
        assertNotNull(responseById);
        assertEquals(200, responseById.getStatusCodeValue());
        assertEquals(dress, responseById.getBody());

        // Find by name
        List<Item> items = Arrays.asList(dress, beautifulDress);
        when(itemRepository.findByName(DRESS)).thenReturn(items);
        ResponseEntity<List<Item>> responseByName = itemController.getItemsByName(DRESS);
        assertNotNull(responseByName);
        assertEquals(200, responseByName.getStatusCodeValue());
        assertEquals(items, responseByName.getBody());
    }

    @Test
    public void testFindItemNotFound() {
        // Find by id
        ResponseEntity<Item> responseById = itemController.getItemById(1L);
        assertNull(responseById.getBody());
        assertEquals(404, responseById.getStatusCodeValue());

        // Find by name
        ResponseEntity<List<Item>> responseByName = itemController.getItemsByName(DRESS);
        assertNull(responseByName.getBody());
        assertEquals(404, responseByName.getStatusCodeValue());
    }

    private List<Item> createItemList() {
        Item dress = new Item(1L, DRESS, new BigDecimal(88.8));
        Item shoe = new Item(2L, SHOE, new BigDecimal(77.7));
        Item shirt = new Item(3L, SHIRT, new BigDecimal(66.6));
        return Arrays.asList(dress, shoe, shirt);
    }
}
