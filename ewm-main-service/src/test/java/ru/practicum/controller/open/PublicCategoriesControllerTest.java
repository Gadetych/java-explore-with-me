package ru.practicum.controller.open;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.not_found.NotFoundException;
import ru.practicum.service.CategoriesService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PublicCategoriesController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PublicCategoriesControllerTest {
    private final MockMvc mvc;
    @MockBean
    private final CategoriesService service;

    String baseUri = "/categories";
    String name = "name";
    long id = 1;
    CategoryDto responseBody = CategoryDto.builder()
            .id(id)
            .name(name)
            .build();

    private MockHttpServletRequestBuilder setRequestHeadersWithoutBody(MockHttpServletRequestBuilder builder) {
        return builder
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON);
    }

    @Test
    void findAll_shouldReturnStatusOk() throws Exception {
        List<CategoryDto> response = List.of(responseBody);
        int from = 0;
        int size = 1;
        when(service.findAll(from, size)).thenReturn(response);

        mvc.perform(setRequestHeadersWithoutBody(get(String.format(baseUri + "?from=%d&size=%d", from, size))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value(name));
    }

    @Test
    void find_shouldReturnStatusOk() throws Exception {
        when(service.findById(id)).thenReturn(responseBody);

        mvc.perform(setRequestHeadersWithoutBody(get(baseUri + "/" + id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void find_shouldReturnStatusNotFound() throws Exception {
        when(service.findById(id)).thenThrow(new NotFoundException(String.format("Category with id=%d was not found", id)));

        mvc.perform(setRequestHeadersWithoutBody(get(baseUri + "/" + id)))
                .andExpect(status().isNotFound());
    }
}