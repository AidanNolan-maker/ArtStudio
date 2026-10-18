package com.artstudio.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Artwork {
    private final long id;
    private final String title;
    private final byte[] imageData;
    private final int width;
    private final int height;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Override
    public String toString() {
        return title + " (ID: " + id + ")";
    }
}
