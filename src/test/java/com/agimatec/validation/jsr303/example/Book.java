package com.agimatec.validation.jsr303.example;

import com.agimatec.validation.constraints.Length;
import com.agimatec.validation.constraints.NotEmpty;
import com.agimatec.validation.constraints.NotNull;

import javax.validation.GroupSequence;
import javax.validation.Valid;

@GroupSequence(name = "default", sequence = {"first", "second", "last"})
public class Book {
    private String title;
    @Length(max = 30, groups = "second")
    private String subtitle;
    @Valid
    @NotNull(groups = "first")
    private Author author;

    @NotEmpty(groups = "first")
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
}