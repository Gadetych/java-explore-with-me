package ru.practicum.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.dto.admin.NewUserRequest;
import ru.practicum.dto.admin.UserDto;
import ru.practicum.service.AdminUsersService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUsersController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AdminUsersControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private final AdminUsersService service;

    String baseUri = "/admin/users";
    long id = 1L;
    String username = "test";
    String email = "test@test.com";
    UserDto dto = UserDto.builder()
            .id(id)
            .email(email)
            .name(username)
            .build();

    private MockHttpServletRequestBuilder setRequestHeadersWithoutBody(MockHttpServletRequestBuilder builder) {
        return builder
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder setRequestHeadersWithBody(MockHttpServletRequestBuilder builder, Object body) throws JsonProcessingException {
        return builder
                .content(mapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON);
    }

    @Test
    void findAllUsers_shouldReturnStatus200() throws Exception {
        List<Long> array = List.of(id);
        int from = 0;
        int size = 1;
        String uriGetAllUsers = String.format("%s?array=%d&from=%d&size=%d", baseUri, array.get(0), from, size);
        System.out.println("Логирование uri = " + uriGetAllUsers);
        when(service.findAllUsers(array, from, size)).thenReturn(List.of(dto));
        mvc.perform(setRequestHeadersWithoutBody(get(uriGetAllUsers)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].email").value(email))
                .andExpect(jsonPath("$[0].name").value(username));
    }

    @Test
    void findAllUsersWithDefaultValue_shouldReturnStatus200() throws Exception {
        List<Long> array = List.of(id);
        String uriGetAllUsers = String.format("%s?array=%d", baseUri, array.get(0));
        System.out.println("Логирование uri = " + uriGetAllUsers);
        when(service.findAllUsers(array, 0, 10)).thenReturn(List.of(dto));
        mvc.perform(setRequestHeadersWithoutBody(get(uriGetAllUsers)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].email").value(email))
                .andExpect(jsonPath("$[0].name").value(username));
    }

    private NewUserRequest createNewUserRequest() {
        return NewUserRequest.builder()
                .email(email)
                .name(username)
                .build();
    }

    @Test
    void createUser_shouldReturnStatus201() throws Exception {
        NewUserRequest requestBody = createNewUserRequest();
        when(service.createUser(requestBody))
                .thenReturn(dto);
        mvc.perform(setRequestHeadersWithBody(post(baseUri), requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value(username));

    }

    @Test
    void createUserNotValidEmail_shouldReturnStatus400() throws Exception {
        NewUserRequest requestBody = createNewUserRequest();
        requestBody.setEmail("test.com");
        mvc.perform(setRequestHeadersWithBody(post(baseUri), requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_shouldReturnStatusNoContent() throws Exception {
        mvc.perform(setRequestHeadersWithoutBody(delete(baseUri + "/" + id)))
                .andExpect(status().isNoContent());
        verify(service, times(1)).deleteUser(id);
    }
}