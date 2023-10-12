package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.enums.State;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;
import ru.practicum.ewm.util.Create;
import ru.practicum.ewm.util.Update;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("admin/")
@RequiredArgsConstructor
@Validated
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;
    private final CommentService commentService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        return userService.addUser(newUserRequest);
    }

    @GetMapping("/users")
    public List<UserDto> getAllUsers(@RequestParam(required = false) List<Long> ids,
                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        return userService.getAllUsers(ids, from, size);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Validated({Create.class})
                                   @RequestBody NewCategoryDto newCategoryDto) {
        return categoryService.addCategory(newCategoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") @Positive Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable("catId") @Positive Long catId,
                                      @Validated({Update.class}) @RequestBody NewCategoryDto newCategoryDto) {
        return categoryService.updateCategory(catId, newCategoryDto);
    }

    @GetMapping("/events")
    public List<EventFullDto> getAdminEvents(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<State> states,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(value = "rangeStart", required = false)
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                             @RequestParam(value = "rangeEnd", required = false)
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                             @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(defaultValue = "10") @Min(1) Integer size) {

        return eventService.getAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return compilationService.addCompilation(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable("compId") Long compId,
                                            @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        return compilationService.updateCompilation(compId, updateCompilationRequest);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}
