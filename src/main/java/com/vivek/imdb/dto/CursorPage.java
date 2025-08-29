package com.vivek.imdb.dto;

import jakarta.annotation.Nullable;

import java.util.List;

public record CursorPage<T, R>(List<T> item,@Nullable R nextCursor) {
}
