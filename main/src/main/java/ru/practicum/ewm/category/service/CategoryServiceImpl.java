package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ObjectNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.category.mapper.CategoryMapper.toCategory;
import static ru.practicum.ewm.category.mapper.CategoryMapper.toCategoryDto;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        String name = newCategoryDto.getName();

        if (categoryRepository.existsByName(name)) {
            throw new ConflictException(
                    "Category name already exists"
            );
        }

        Category category = toCategory(newCategoryDto);

        return toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new ObjectNotFoundException(String.format(
                    "Category with ID: %s was not found", catId
            ));
        }

         if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new ConflictException(
                    "The category is not empty"
            );
        }

        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format(
                    "Category with ID: %s was not found", catId
            ));
        });

        if (!category.getName().equals((newCategoryDto.getName()))) {
            if (categoryRepository.existsByName(newCategoryDto.getName())) {
                throw new ConflictException(
                        "There is already a category named - " + newCategoryDto.getName()
                );
            }
        }

        category.setName(newCategoryDto.getName());

        return toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format(
                    "Category with ID: %s was not found", catId
            ));
        });

        return toCategoryDto(category);
    }
}
