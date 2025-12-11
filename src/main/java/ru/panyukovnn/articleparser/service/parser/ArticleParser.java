package ru.panyukovnn.articleparser.service.parser;

import ru.panyukovnn.articleparser.dto.ArticleContent;
import ru.panyukovnn.articleparser.dto.ArticleSource;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleParser {

    List<ArticleContent> loadContent(LocalDateTime dateFrom);

    ArticleSource getSource();
}
