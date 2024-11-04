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
import ru.practicum.dto.admin.CategoryDto;
import ru.practicum.dto.admin.NewCategoryDto;
import ru.practicum.service.AdminCategoriesService;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCategoriesController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AdminCategoryControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private final AdminCategoriesService service;

    String baseUri = "/admin/categories";
    String name = "name";
    NewCategoryDto requestBody = NewCategoryDto.builder()
            .name(name)
            .build();
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

    private MockHttpServletRequestBuilder setRequestHeadersWithBody(MockHttpServletRequestBuilder builder, Object body) throws JsonProcessingException {
        return builder
                .content(mapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON);
    }

    @Test
    void create_shouldReturnStatusCreated() throws Exception {
        when(service.create(requestBody)).thenReturn(responseBody);

        mvc.perform(setRequestHeadersWithBody(post(baseUri), requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));
    }


    @Test
    void delete_shouldReturnStatusNoContent() throws Exception {
        mvc.perform(setRequestHeadersWithoutBody(delete(baseUri + "/" + id)))
                .andExpect(status().isNoContent());
        verify(service, times(1)).delete(id);
    }

    @Test
    void update_shouldReturnStatusOk() throws Exception {
        CategoryDto requestBodyUpdate = CategoryDto.builder()
                .id(id)
                .name("new_name")
                .build();
        when(service.update(requestBodyUpdate)).thenReturn(requestBodyUpdate);

        mvc.perform(setRequestHeadersWithBody(patch(baseUri + "/" + id), requestBodyUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("new_name"));
    }
}