package com.tw.api.unit.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import com.tw.api.unit.test.services.ShowService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
class ToDoControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private TodoController todoController;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ShowService showService;

    @MockBean
    private TodoRepository todoRepository;

    @Test
    void getAllResource() throws Exception {
        List<Todo> todos = new ArrayList<>();
        todos.add(new Todo("sample title", false));

        when(todoRepository.getAll()).thenReturn(todos);
        ResultActions result = mvc.perform(get("/todos"));
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is( "sample title")));
    }

    @Test
    void getTodoIsFound() throws Exception {
        Todo todo = new Todo(1, "title",  true, 2);
        Optional<Todo> todoOptional = Optional.of(todo);

        when(todoRepository.findById(1)).thenReturn(todoOptional);
        ResultActions result = mvc.perform(get("/todos/1"));
        result.andExpect(status().isOk());
    }

    @Test
    void getTodoIsNotFound() throws Exception {
        Todo todo = new Todo(1, "title",  true, 2);
        Optional<Todo> todoOptional = Optional.of(todo);

        when(todoRepository.findById(2)).thenReturn(todoOptional);
        ResultActions result = mvc.perform(get("/todos/1"));
        result.andExpect(status().isNotFound());
    }

    @Test
    void saveTodo() throws Exception {
        Todo todo = new Todo(1, "title",  true, 2);

        ResultActions result = mvc.perform(post("/todos")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(todo)));

        result.andExpect(status().isCreated()).andDo(print());
    }


    @Test
    void deleteOneTodoIsFound() throws Exception {
        Todo todo = new Todo(1, "title",  true, 2);
        Optional<Todo> todoOptional = Optional.of(todo);

        when(todoRepository.findById(1)).thenReturn(todoOptional);
        ResultActions result = mvc.perform(delete("/todos/1"));
        result.andExpect(status().isOk());
   }

    @Test
    void deleteOneTodoIsNotFound() throws Exception {
        Todo todo = new Todo(1, "title",  true, 2);
        Optional<Todo> todoOptional = Optional.of(todo);

        when(todoRepository.findById(2)).thenReturn(todoOptional);
        ResultActions result2 = mvc.perform(delete("/todos/1"));
        result2.andExpect(status().isNotFound());
    }

    @Test
    void updateTodoIsFound() throws Exception {
        Todo todo = new Todo(1, "title",  true, 2);
        Todo todo2 = new Todo(1, "title2",  true, 2);
        List<Todo> todolist = new ArrayList<>();
        todolist.add(todo);

        when(todoRepository.findById(1)).thenReturn(todolist.stream().findFirst());
        ResultActions result = mvc.perform(patch("/todos/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(todo2)));

        result.andExpect(status().isOk()).andDo(print());
    }

    @Test
    void updateTodoIsNotFound() throws Exception {
        Todo todo = new Todo(1, "title",  true, 2);
        Todo todo2 = new Todo(1, "title2",  true, 2);
        List<Todo> todolist = new ArrayList<>();
        todolist.add(todo);

        when(todoRepository.findById(2)).thenReturn(todolist.stream().findFirst());
        ResultActions result = mvc.perform(patch("/todos/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(todo2)));

        result.andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    void updateTodoIsNull() throws Exception {
        Todo todo = new Todo(1, "title",  true, 2);
        Todo todo2 = new Todo(1, "title2",  true, 2);
        List<Todo> todolist = new ArrayList<>();
        todolist.add(todo);

        when(todoRepository.findById(2)).thenReturn(todolist.stream().findFirst());
        ResultActions result = mvc.perform(patch("/todos/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(null)));

        result.andExpect(status().isBadRequest()).andDo(print());
    }
}