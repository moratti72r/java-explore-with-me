package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.service.categoryservice.CategoryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/admin/categories")
@Validated
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody NewCategoryDto categoryDto) {
        log.info("Получен POST запрос /admin/categories");
        return categoryService.create(categoryDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        log.info("Получен DELETE запрос /admin/categories/{}", id);
        categoryService.delete(id);
    }

    @PatchMapping("/{id}")
    public CategoryDto patch(@PathVariable("id") long id, @Valid @RequestBody NewCategoryDto categoryDto) {
        log.info("Получен PATCH запрос /admin/categories/{}", id);
        return categoryService.upDate(id, categoryDto);
    }
}
