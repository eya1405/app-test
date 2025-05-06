package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // On injecte le BookService (mocké via la configuration juste en dessous)
    @Autowired
    private BookService bookService;

    // ✅ Cette configuration fournit un faux (mock) BookService à Spring
    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        public BookService bookService() {
            return Mockito.mock(BookService.class);
        }
    }

    @Test
    public void testGetAllBooks() throws Exception {
        List<Book> books = Arrays.asList(
                new Book(1L, "The Great Gatsby", "F. Scott Fitzgerald"),
                new Book(2L, "1984", "George Orwell")
        );

        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("The Great Gatsby")))
                .andExpect(jsonPath("$[1].title", is("1984")));
    }

    @Test
    public void testGetBookById_Found() throws Exception {
        Book book = new Book(1L, "The Great Gatsby", "F. Scott Fitzgerald");
        when(bookService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("The Great Gatsby")))
                .andExpect(jsonPath("$.author", is("F. Scott Fitzgerald")));
    }

    @Test
    public void testGetBookById_NotFound() throws Exception {
        when(bookService.getBookById(3L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}