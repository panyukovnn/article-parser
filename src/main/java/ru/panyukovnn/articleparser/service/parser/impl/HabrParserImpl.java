package ru.panyukovnn.articleparser.service.parser.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.panyukovnn.articleparser.dto.ArticleContent;
import ru.panyukovnn.articleparser.dto.ArticleSource;
import ru.panyukovnn.articleparser.service.autodatafinder.RssMetadataFinder;
import ru.panyukovnn.articleparser.service.autodatafinder.impl.HabrRssMetadataFinder;
import ru.panyukovnn.articleparser.service.loader.ArticlePayloadLoader;
import ru.panyukovnn.articleparser.service.loader.impl.HabrArticlePayloadLoader;
import ru.panyukovnn.articleparser.service.parser.ArticleParser;
import ru.panyukovnn.articleparser.util.LanguageUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class HabrParserImpl implements ArticleParser {

    private final RssMetadataFinder habrRssMetadataFinder = new HabrRssMetadataFinder();
    private final ArticlePayloadLoader habrArticlePayloadLoader = new HabrArticlePayloadLoader();

    @Override
    public List<ArticleContent> loadContent(LocalDateTime dateFrom) {
        List<ArticleContent> articleContents = habrRssMetadataFinder.fetchBasicArticlesInfoSince(dateFrom);

        articleContents.forEach(articleContent -> {
            String payload = habrArticlePayloadLoader.loadArticlePayload(articleContent.getLink());

            articleContent.setPayload(payload);
            articleContent.setLang(LanguageUtils.detectLangByLettersCount(payload));
        });

        return articleContents;
    }

    @Override
    public ArticleSource getSource() {
        return null;
    }
}
