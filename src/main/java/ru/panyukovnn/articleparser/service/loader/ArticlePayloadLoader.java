package ru.panyukovnn.articleparser.service.loader;

import ru.panyukovnn.articleparser.dto.ArticleSource;

public interface ArticlePayloadLoader {

    String loadArticlePayload(String link);

    ArticleSource getSource();
}
