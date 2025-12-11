package ru.panyukovnn.articleparser.service.loader.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.panyukovnn.articleparser.dto.ArticleSource;
import ru.panyukovnn.articleparser.exception.BusinessException;
import ru.panyukovnn.articleparser.service.loader.ArticlePayloadLoader;

@Slf4j
@RequiredArgsConstructor
public class HabrArticlePayloadLoader implements ArticlePayloadLoader {

    public String loadArticlePayload(String link) {
        try {
            Document doc = Jsoup.connect(link)
                .userAgent("Mozilla/5.0")
                .get();

            String payload = doc.select("div.article-body").text();

            log.debug("Загружено содержимое статьи с habr по ссылке: {}", link);

            return payload;
        } catch (Exception e) {
            throw new BusinessException("Не удалось загрузить содержимое статьи с Habr: " + e.getMessage(), e);
        }
    }

    @Override
    public ArticleSource getSource() {
        return ArticleSource.HABR;
    }
}
