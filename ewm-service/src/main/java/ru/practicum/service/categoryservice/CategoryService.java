package ru.practicum.service.categoryservice;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    void delete(long id);

    CategoryDto upDate(long id, NewCategoryDto newCategoryDto);

    List<CategoryDto> getAll(int from, int size);

    CategoryDto getById(long id);
}
