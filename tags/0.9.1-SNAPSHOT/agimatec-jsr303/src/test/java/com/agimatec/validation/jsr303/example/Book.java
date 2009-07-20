package com.agimatec.validation.jsr303.example;

import com.agimatec.validation.constraints.NotEmpty;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@GroupSequence(value = {First.class, Second.class, Last.class})
public class Book {
    @NotNull(groups = First.class)
    @NotEmpty(groups = First.class)
    private String title;

    @Size(max = 30, groups = Second.class)
    private String subtitle;

    @Valid
    @NotNull(groups = First.class)
    private Author author;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @GroupSequence(value = {First.class, Second.class, Last.class})
    public interface All {
    }
}