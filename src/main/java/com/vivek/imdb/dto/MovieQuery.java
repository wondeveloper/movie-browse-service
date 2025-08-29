package com.vivek.imdb.dto;


public sealed interface MovieQuery permits CursorQuery, OffsetQuery {
}

