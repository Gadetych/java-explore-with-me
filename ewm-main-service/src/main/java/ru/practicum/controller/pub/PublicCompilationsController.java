package ru.practicum.controller.pub;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.PublicCompilationParam;
import ru.practicum.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationsController {
    private final CompilationService service;

    @GetMapping
    public List<CompilationDto> findAll(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                        @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                        @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return service.findAll(PublicCompilationParam.builder()
                .pinned(pinned)
                .from(from)
                .size(size)
                .build());
    }

    @GetMapping("/{compId}")
    public CompilationDto findById(@PathVariable("compId")
                                   @Positive long compId) {
        return service.findById(compId);
    }
}
