package ru.panyukovnn.articleparser.service.autodatafinder.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.panyukovnn.articleparser.dto.ArticleContent;
import ru.panyukovnn.articleparser.dto.ArticleSource;
import ru.panyukovnn.articleparser.exception.BusinessException;
import ru.panyukovnn.articleparser.service.autodatafinder.RssMetadataFinder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class HabrRssMetadataFinder implements RssMetadataFinder {

    private static final String JAVA_HUB = "java";
    private static final String HABR_RSS_LINK = "https://habr.com/ru/rss/hubs/%s/articles/all/?limit=100";

    // RSS дата: "Thu, 11 Dec 2025 10:00:00 GMT"
    private static final DateTimeFormatter RSS_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    /**
     * Загружает статьи из указанного хаба, начиная с определённой даты.
     *
     * @param fromDate дата, начиная с которой загружать статьи (включительно)
     * @return список статей, отсортированных по дате (новые первыми)
     */
    public List<ArticleContent> fetchBasicArticlesInfoSince(LocalDateTime fromDate) {
        String rssUrl = HABR_RSS_LINK.formatted(JAVA_HUB);

        try {
            HttpResponse<InputStream> response = fetchRss(rssUrl);
            NodeList items = extractNodeListItemsFromResponse(response);

            List<ArticleContent> articles = new ArrayList<>();

            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);

                ArticleContent articleContent = parseItem(item);

                // Фильтр: статьи с датой >= fromDate
                if (!articleContent.getPublicationDate().isBefore(fromDate)) {
                    articles.add(articleContent);
                }
            }

            return articles;
        } catch (ParserConfigurationException | IOException | SAXException | InterruptedException e) {
            throw new BusinessException("Ошибка парсинга статей Habr из RSS ленты: " + e.getMessage(), e);
        }
    }

    private NodeList extractNodeListItemsFromResponse(HttpResponse<InputStream> response) throws ParserConfigurationException, SAXException, IOException {
        InputStream inputStream = response.body();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(inputStream);
        doc.getDocumentElement().normalize();

        return doc.getElementsByTagName("item");
    }

    private HttpResponse<InputStream> fetchRss(String rssUrl) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(rssUrl))
            .header("User-Agent", "Mozilla/5.0 (compatible; HabrRssLoader/1.0)")
            .header("Accept", "application/rss+xml, application/xml")
            .timeout(Duration.ofSeconds(30))
            .GET()
            .build();

        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            throw new BusinessException("Ошибка получения RSS ленты habr: " + response.statusCode());
        }

        return response;
    }

    private ArticleContent parseItem(Element item) {
        String title = getElementText(item, "title");
        String link = getElementText(item, "link");
        String author = getElementText(item, "dc:creator");
        String pubDateStr = getElementText(item, "pubDate");
        String description = stripHtml(getElementText(item, "description"));

        LocalDateTime pubDate = ZonedDateTime.parse(pubDateStr, RSS_DATE_FORMAT).toLocalDateTime().plusHours(3); // в Мск

        return ArticleContent.builder()
            .title(title)
            .link(link)
            .author(author)
            .source(ArticleSource.HABR)
            .publicationDate(pubDate)
            .description(description)
            .build();
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);

        if (nodes.getLength() > 0 && nodes.item(0) != null) {
            return nodes.item(0).getTextContent().trim();
        }

        return "";
    }

    private String stripHtml(String html) {
        return html == null ? "" : html.replaceAll("<[^>]*>", "").trim();
    }
}
