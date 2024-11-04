package ru.practicum.service.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.admin.NewUserRequest;
import ru.practicum.dto.admin.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "spring.profiles.active=test")
@Transactional
class AdminUsersServiceImplTest {
    @Autowired
    private AdminUsersServiceImpl service;
    String name1 = "name1";
    String name2 = "name2";
    String name3 = "name3";
    String email1 = "email1@email.com";
    String email2 = "email2@email.com";
    String email3 = "email3@email.com";
    NewUserRequest request1 = NewUserRequest.builder()
            .name(name1)
            .email(email1)
            .build();
    NewUserRequest request2 = NewUserRequest.builder()
            .name(name2)
            .email(email2)
            .build();
    NewUserRequest request3 = NewUserRequest.builder()
            .name(name3)
            .email(email3)
            .build();
    long id1;
    long id2;
    long id3;

    @BeforeEach
    void setUp() {
        id1 = service.createUser(request1).getId();
        id2 = service.createUser(request2).getId();
        id3 = service.createUser(request3).getId();
    }

    @Test
    void findAllUsers_whenIdsNotEmpty_thenShouldReturnListSize3() {
        List<Long> ids = List.of(id1, id2, id3);
        int from = 0;
        int size = 10;
        List<UserDto> result = service.findAllUsers(ids, from, size);

        assertFalse(result.isEmpty());
        assertEquals(3, result.size());
    }

    @Test
    void findAllUsers_whenIdsIsEmpty_thenShouldReturnPageListSize2() {
        List<Long> ids = null;
        int from = 1;
        int size = 3;
        List<UserDto> result = service.findAllUsers(ids, from, size);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(id2, result.get(0).getId());
        assertEquals(id3, result.get(1).getId());
    }

    @Test
    void createUser_shouldCreateNewUser() {
        String name4 = "name4";
        String email4 = "email4@email.com";
        NewUserRequest request4 = NewUserRequest.builder()
                .name(name4)
                .email(email4)
                .build();
        UserDto result = service.createUser(request4);

        assertNotNull(result);
        assertEquals(id3 + 1, result.getId());
    }

    @Test
    void createUser_whenEmailNotUnique_thenShouldThrowException() {
        assertThrows(DataIntegrityViolationException.class, () -> service.createUser(request3));
    }

    @Test
    void deleteUser() {
        service.deleteUser(id1);

        List<UserDto> result = service.findAllUsers(null, 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(id2, result.get(0).getId());
        assertEquals(id3, result.get(1).getId());
    }
}