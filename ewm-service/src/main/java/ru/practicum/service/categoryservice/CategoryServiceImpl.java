package ru.practicum.service.categoryservice;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.mapper.CategoryMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto create(NewCategoryDto categoryDto) {
        Category category = categoryRepository.save(CategoryMapper.toCategory(categoryDto));
        log.info("Категория с id={} успешно добавлена", category.getId());

        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void delete(long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            log.info("Категория с id={} удалена", id);
        } else throw new NotFoundException("Category with id=" + id + "was not found");
    }

    @Override
    public CategoryDto upDate(long id, NewCategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category with id=" + id + "was not found"));
        category.setName(categoryDto.getName());

        Category result = categoryRepository.save(category);
        log.info("Категория с id={} успешно изменена", id);

        return CategoryMapper.toCategoryDto(result);
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {

        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("id").ascending());

        log.info("Получен список категорий");
        return categoryRepository.findAll(pageRequest).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
        log.info("Категория с id={} получена", id);
        return CategoryMapper.toCategoryDto(category);

    }
}
