package com.vivek.imdb.util;

import com.vivek.imdb.dto.*;
import com.vivek.imdb.exceptions.CursorNotFoundException;
import com.vivek.imdb.exceptions.InvalidCursorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Slf4j
public class RequestValidator {


    public static UnaryOperator<Mono<MovieDto>> validate(){
        return movieDtoMono -> movieDtoMono.filter(checkMovieTitle())
                .switchIfEmpty(ApplicationException.invalidTitle())
                .filter(hasValidReleaseYear())
                .switchIfEmpty(ApplicationException.invalidYear());

    }

    public static UnaryOperator<Mono<MovieQueryDto>> validateClientCursor(){
        return movieQueryMono -> movieQueryMono.filter(validateCursor())
                .switchIfEmpty(Mono.error(new CursorNotFoundException("Cursor is passed as null")))
                .filter(validateParsable())
                .switchIfEmpty(Mono.error(new InvalidCursorException("Cursor is invalid. Can't be parsed")));
    }

    public static Function<Mono<MovieQueryDto>, Mono<MovieQuery>> validateClientCursorV2(){

        return mono -> mono.flatMap(movieQ -> {
            //if cursor is null
            if (!validateCursor().test(movieQ)){
                return Mono.just(new OffsetQuery(movieQ.page(), movieQ.size(),movieQ.sort()));
            }

            //if cursor is not null, but should be a valid one

           if (!validateParsable().test(movieQ)){
               return Mono.error(new InvalidCursorException("Cursor is invalid. Can't be parsed"));
           }
           return Mono.just(new CursorQuery(movieQ.cursorB64()));
        });
    }

    //not required because if the cursor is correct, we ignore the other details
    private static Predicate<? super MovieQueryDto> validateDetailsAreSame() {
        return query -> {
            try {
                TokenPayload tokenPayload = CursorUtil.decodePayloadCursor(query.cursorB64());
            }catch (Exception e){
                log.error("Exception while decoding");
                return false;
            }
            return true;
        };
    }

    private static Predicate<? super MovieQueryDto> validateParsable() {
        return query -> {
            try {
                CursorUtil.decodePayloadCursor(query.cursorB64());
            }catch (Exception e){
                log.error("Cursor is not parsable");
                return false;
            }
            return true;
        };
    }



    private static Predicate<? super MovieQueryDto> validateCursor() {
        return movieQuery -> null != movieQuery.cursorB64() && !movieQuery.cursorB64().isEmpty();
    }

    private static Predicate<MovieDto> checkMovieTitle() {
        return p -> p.getTitle() != null && !p.getTitle().isBlank();
    }

    private static Predicate<MovieDto> hasValidReleaseYear() {
        return p -> {
            String y = p.getReleaseYear();
            if (y == null || y.isBlank()) return false;
            try {
                int year = Integer.parseInt(y);
                log.info("Movie year : {}", year);
                int current = java.time.Year.now().getValue();
                return year >= 1900 && year <= current; // tweak bounds as you like
            } catch (NumberFormatException e) {
                log.error("Can't parse the year: {}", y);
                return false;
            }
        };
    }

    private record ResolvedQuery(int page, int size, Sort sort, MovieQueryDto original) {}

//    private static Predicate<MovieDto> invalidMovieYear() {
//        return p -> Objects.isNull(p.getReleaseYear()) || checkIfTheYearIsValid(p.getReleaseYear()) || p.getReleaseYear().isEmpty() || Integer.parseInt(p.getReleaseYear()) < 2025;
//    }
//
//    private static boolean checkIfTheYearIsValid(String releaseYear) {
//        try {
//            int year = Integer.parseInt(releaseYear);
//            log.info("Movie year : {}", year);
//        }catch (Exception e){
//            log.error("Can't parse the year");
//            return false;
//        }
//        return true;
//    }

}
