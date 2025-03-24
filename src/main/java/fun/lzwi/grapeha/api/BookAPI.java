package fun.lzwi.grapeha.api;

import fun.lzwi.epubime.epub.EpubChapter;
import fun.lzwi.epubime.epub.EpubParseException;
import fun.lzwi.epubime.epub.EpubParser;
import fun.lzwi.grapeha.AuthUtils;
import fun.lzwi.grapeha.db.repository.BookRepository;
import fun.lzwi.grapeha.library.CacheUtils;
import fun.lzwi.grapeha.library.reader.EpubReader;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.JWTAuthHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BookAPI {
  static Logger logger = LoggerFactory.getLogger(BookAPI.class);

  public static void init(Router router, Vertx vertx) {
    router.route("/api/v1/books").handler(JWTAuthHandler.create(AuthUtils.getProvider(vertx)));
    router
        .get("/api/v1/books/:bookId")
        .respond(
            ctx -> {
              String bookId = ctx.pathParam("bookId");
              return BookRepository.getInstance()
                  .findById(bookId)
                  .map(
                      book ->
                          new JsonObject()
                              .put("code", 200)
                              .put("msg", "获取书籍成功。")
                              .put("data", book));
            });

    router
        .get("/api/v1/books/:bookId/cover")
        .produces("image/jpeg")
        .produces("image/png")
        .handler(
            ctx -> {
              String bookId = ctx.pathParam("bookId");
              BookRepository.getInstance()
                  .findById(bookId)
                  .map(book -> CacheUtils.getBookCoverPath(book.getPath()))
                  .compose(cover -> vertx.fileSystem().readFile(cover))
                  .onSuccess(
                      buffer -> {
                        HttpServerResponse response = ctx.response();
                        String acceptableContentType = ctx.getAcceptableContentType();
                        response.putHeader("content-type", acceptableContentType);
                        response.end(buffer);
                      })
                  .onFailure(
                      e -> {
                        HttpServerResponse response = ctx.response();
                        response.end("fail");
                        logger.error("获取BookId: %s封面失败！".formatted(bookId), e);
                      });
            });

    router
        .get("/api/v1/books")
        .respond(
            ctx ->
                BookRepository.getInstance()
                    .findAll()
                    .map(
                        books -> {
                          JsonObject resp = new JsonObject();
                          resp.put("code", 200);
                          resp.put("msg", "获取书籍列表成功。");
                          resp.put("data", books);
                          return resp;
                        }));

    router
        .get("/api/v1/books/:bookId/content")
        .respond(
            ctx -> {
              String bookId = ctx.pathParam("bookId");
              JsonObject resp = new JsonObject();

              return BookRepository.getInstance()
                  .findById(bookId)
                  .map(
                      book -> {
                        try {
                          EpubParser parser = new EpubParser(new File(book.getPath()));
                          fun.lzwi.epubime.epub.EpubBook epubBook = parser.parse();
                          List<EpubChapter> chapters = epubBook.getChapters();
                          resp.put("code", 200);
                          resp.put("msg", "获取书籍目录成功。");
                          resp.put("data", chapters);
                        } catch (EpubParseException e) {
                          resp.put("code", 400);
                          resp.put("msg", e.getMessage());
                        }

                        return resp;
                      });
            });

//    router
//        .get("/api/v1/books/:bookId/resources")
//        .respond(
//            ctx -> {
//              String bookId = ctx.pathParam("bookId");
//              return BookRepository.getInstance()
//                  .findById(bookId)
//                  .onFailure(Throwable::printStackTrace)
//                  .map(
//                      book -> {
//                        JsonObject resp = new JsonObject();
//                        try {
//                          EasyEpub epub = new EasyEpub(book.getPath());
//                          List<EasyEpub.EasyResource> resources = epub.getResources();
//                          resp.put("code", 200);
//                          resp.put("msg", "");
//                          resp.put("data", resources);
//                        } catch (ParserConfigurationException | SAXException | IOException e) {
//                          logger.error("获取BookId: %s资源失败！".formatted(bookId), e);
//                          resp.put("code", 400);
//                          resp.put("msg", e.getMessage());
//                        }
//                        return resp;
//                      });
//            });

    router
        .getWithRegex("/api/v1/books/(?<bookId>[^\\\\/]+)/resources/(?<href>.*)")
        .respond(
            ctx -> {
              String bookId = ctx.pathParam("bookId");
              String href = ctx.pathParam("href");
              return BookRepository.getInstance()
                  .findById(bookId)
                  .compose(
                      book -> {
                        ctx.response()
                            .putHeader(
                                "content-type", EpubReader.getResourceType(book.getPath(), href));
                        return EpubReader.getResource(book.getPath(), href);
                      })
                  .compose(
                      buffer -> {
                        if (ctx.response()
                            .headers()
                            .get("content-type")
                            .equals("application/xhtml+xml")) {
                          String content = buffer.toString(StandardCharsets.UTF_8);
                          logger.info("content: %s".formatted(content));
                          return Future.succeededFuture(content);
                        }
                        return Future.succeededFuture(buffer);
                      });
            });
  }
}
