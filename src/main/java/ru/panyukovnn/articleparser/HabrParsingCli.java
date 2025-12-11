package ru.panyukovnn.articleparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import picocli.CommandLine;
import ru.panyukovnn.articleparser.dto.ArticleContent;
import ru.panyukovnn.articleparser.exception.BusinessException;
import ru.panyukovnn.articleparser.service.parser.ArticleParser;
import ru.panyukovnn.articleparser.service.parser.impl.HabrParserImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "article-parser",
    description = "Загружает и очищает субтитры из YouTube видео",
    mixinStandardHelpOptions = true,
    version = "1.0"
)
public class HabrParsingCli implements Callable<Integer> {

    private final ArticleParser habrParser = new HabrParserImpl();
    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    @CommandLine.Parameters(
        index = "0",
        description = "Date from which to load habr articles in LocalDateTime format"
    )
    private LocalDateTime dateFrom;

    @Override
    public Integer call() {
        try {
            List<ArticleContent> articleContents = habrParser.loadContent(dateFrom);

            // Выводим результат парсинга стаей
            System.out.println(objectMapper.writeValueAsString(articleContents));

            return 0;
        } catch (BusinessException e) {
            System.err.println("Бизнес исключение при парсинге статей с Habr: " + e.getMessage());

            return 1;
        } catch (Exception e) {
            // Неожиданная ошибка - выводим в stderr
            System.err.println("Непредвиденное исключение: " + e.getMessage());
            e.printStackTrace(System.err);

            return 2;
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new HabrParsingCli()).execute(args);
        System.exit(exitCode);
    }
}
