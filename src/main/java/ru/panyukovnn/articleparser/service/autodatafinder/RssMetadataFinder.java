package ru.panyukovnn.articleparser.service.autodatafinder;

import ru.panyukovnn.articleparser.dto.ArticleContent;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Ищет статьи для загрузки
 */
public interface RssMetadataFinder {

    /**
     * Загружает статьи, которые необходимо распарсить
     *
     * @param dateFrom дата, с которой производить загрузку, в Мск
     * @return список ссылок
     */
    List<ArticleContent> fetchBasicArticlesInfoSince(LocalDateTime dateFrom);
}
