package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    private static final String USERNAME = "tester";
    private static final String PASSWORD = "testPassword";
    private static final String CONFIRM_PASSWORD = "testPassword";

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void testCreateUserHappyPath() {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        ResponseEntity<User> response = userController.createUser(createUserRequest());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("tester", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void testCreateUserBadRequest() {
        // Too short password
        CreateUserRequest createUserRequest = createUserRequest();
        createUserRequest.setPassword("test");
        createUserRequest.setConfirmPassword("test");
        ResponseEntity<User> responseTooShortPassword = userController.createUser(createUserRequest);
        assertNotNull(responseTooShortPassword);
        assertEquals(400, responseTooShortPassword.getStatusCodeValue());

        // Wrong confirm password
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setConfirmPassword("wrongPassword");
        ResponseEntity<User> responseWrongPassword = userController.createUser(createUserRequest);
        assertNotNull(responseWrongPassword);
        assertEquals(400, responseWrongPassword.getStatusCodeValue());
    }

    @Test
    public void testFindUserHappyPath() {
        User user = new User(1L, USERNAME, PASSWORD);
        // Find by id
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ResponseEntity<User> responseById = userController.findById(1L);
        assertNotNull(responseById);
        assertEquals(200, responseById.getStatusCodeValue());
        User returnedUser = responseById.getBody();
        assertNotNull(returnedUser);
        assertEquals(1L, returnedUser.getId());
        assertEquals(USERNAME, returnedUser.getUsername());

        // Find by name
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        ResponseEntity<User> responseByName = userController.findByUserName(USERNAME);
        assertNotNull(responseByName);
        assertEquals(200, responseByName.getStatusCodeValue());
        returnedUser = responseByName.getBody();
        assertNotNull(returnedUser);
        assertEquals(1L, returnedUser.getId());
        assertEquals(USERNAME, returnedUser.getUsername());
    }

    @Test
    public void testFindUserNotFound() {
        // Find by id
        ResponseEntity<User> responseById = userController.findById(1L);
        assertNull(responseById.getBody());
        assertEquals(404, responseById.getStatusCodeValue());

        // Find by name
        ResponseEntity<User> responseByName = userController.findByUserName(USERNAME);
        assertNull(responseByName.getBody());
        assertEquals(404, responseByName.getStatusCodeValue());
    }

    private CreateUserRequest createUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USERNAME);
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setConfirmPassword(CONFIRM_PASSWORD);
        return createUserRequest;
    }
}
