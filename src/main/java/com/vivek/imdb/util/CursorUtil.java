package com.vivek.imdb.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vivek.imdb.config.SeekToken;
import com.vivek.imdb.config.TokenPayload;
import com.vivek.imdb.dto.Cursor;
import com.vivek.imdb.dto.MovieQueryDto;
import com.vivek.imdb.dto.PagingMode;
import com.vivek.imdb.dto.SortSpec;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class CursorUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());

    public static Cursor decodeCursor(String cursorB64) {
        String original = new String(Base64.getUrlDecoder().decode(cursorB64), StandardCharsets.UTF_8);
        String[] strings = original.split("\\|");
        return new Cursor(Instant.parse(strings[0]), strings[1]);
    }

    public static String encodeCursor(Cursor cursor){
        String c = cursor.createdIdIso() + "|" + cursor.id();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(c.getBytes());
    }

    public static String encodeObjectNode(ObjectNode cursor){
        return Base64.getUrlEncoder().withoutPadding().encodeToString(cursor.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static ObjectNode decodeObjectNode(String cursor){
        byte[] json = Base64.getDecoder().decode(cursor);
       try {
           return (ObjectNode) MAPPER.readTree(json);
       } catch (IOException e) {
           throw new IllegalArgumentException(e);
       }
    }

    public static String encodePayloadCursor(TokenPayload payload){
        try {
            byte[] json = MAPPER.writeValueAsBytes(payload);
            return Base64.getUrlEncoder().encodeToString(json);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode token", e);
        }
    }

    public static TokenPayload decodePayloadCursor(String payload){
        try {
            byte[] json = Base64.getUrlDecoder().decode(payload);
            return MAPPER.readValue(json , SeekToken.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decode token", e);
        }
    }

//    public static TokenPayload decodePayloadCursor(String payload){
//        try {
//            byte[] json = Base64.getUrlDecoder().decode(payload);
//            return MAPPER.readValue(json , SeekToken.class);
//        } catch (Exception e) {
//            throw new IllegalStateException("Failed to decode token", e);
//        }
//    }


    public static ObjectNode seekToken(Instant created, String id, int size, @Nullable Sort sort) {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("type", "seek");
        n.put("created", created.toString());
        n.put("id", id);
        n.put("size", size);
        if (sort != null) n.put("sort", sort.toString());
        return n;
    }

    public static ObjectNode offsetToken(int nextPage, int size, @Nullable Sort sort) {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("type", "offset");
        n.put("nextPage", nextPage);
        n.put("size", size);
        if (sort != null) n.put("sort", sort.toString());
        return n;
    }

    public static MovieQueryDto createDefaultMovieQuery() {
        return new MovieQueryDto(PagingMode.OFFSET,null, 0, 20, SortSpec.sort(Sort.by("createdAt").ascending()));
    }

    public static MovieQueryDto createDefaultMovieQuerySeek() {
        return new MovieQueryDto(PagingMode.SEEK_CURSOR,null, 0, 20, SortSpec.sort(Sort.by("createdAt").ascending()));
    }
}
