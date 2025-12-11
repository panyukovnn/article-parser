package ru.panyukovnn.articleparser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleContent {

    /**
     * Ссылка на материал
     */
    private String link;
    /**
     * Язык
     */
    private Lang lang;
    /**
     * Тип источника
     */
    private ArticleSource source;
    /**
     * Заголовок
     */
    private String title;
    /**
     * Автор
     */
    private String author;
    /**
     * Описание
     */
    private String description;
    /**
     * Дата публикации (в UTC)
     */
    private LocalDateTime publicationDate;
    /**
     * Содержимое источника
     */
    private String payload;

}
