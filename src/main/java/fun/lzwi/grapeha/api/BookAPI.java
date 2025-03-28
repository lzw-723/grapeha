package fun.lzwi.grapeha.api;

import fun.lzwi.epubime.epub.EpubChapter;
import fun.lzwi.epubime.epub.EpubParseException;
import fun.lzwi.epubime.epub.EpubParser;
import fun.lzwi.grapeha.AuthUtils;
import fun.lzwi.grapeha.db.repository.BookRepository;
import fun.lzwi.grapeha.library.CacheUtils;
import fun.lzwi.grapeha.library.reader.EpubReader;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.JWTAuthHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

public class BookAPI {
  static Logger logger = LoggerFactory.getLogger(BookAPI.class);

  public static void init(Router router, Vertx vertx) {
    router.route("/api/v1/books").handler(JWTAuthHandler.create(AuthUtils.getProvider(vertx)));
    router.get("/api/v1/books/:bookId").respond(ctx -> {
      String bookId = ctx.pathParam("bookId");
      return BookRepository.getInstance().findById(bookId).map(book -> new JsonObject().put("code", 200).put("msg",
        "获取书籍成功。").put("data", book));
    });

    router.get("/api/v1/books/:bookId/cover").produces("image/jpeg").produces("image/png").handler(ctx -> {
      String bookId = ctx.pathParam("bookId");
      BookRepository.getInstance().findById(bookId).map(book -> CacheUtils.getBookCoverPath(book.getPath())).compose(cover -> vertx.fileSystem().readFile(cover)).onSuccess(buffer -> {
        HttpServerResponse response = ctx.response();
        String acceptableContentType = ctx.getAcceptableContentType();
        response.putHeader("content-type", acceptableContentType);
        response.end(buffer);
      }).onFailure(e -> {
        HttpServerResponse response = ctx.response();
        response.end("fail");
        logger.error("获取BookId: %s封面失败！".formatted(bookId), e);
      });
    });

    router.get("/api/v1/books").respond(ctx -> BookRepository.getInstance().findAll().map(books -> {
      // 按照书籍名称排序
      Collator collator = Collator.getInstance(Locale.CHINA);
      books.sort((b1, b2) -> collator.compare(b1.getName(), b2.getName()));
      JsonObject resp = new JsonObject();
      resp.put("code", 200);
      resp.put("msg", "获取书籍列表成功。");
      resp.put("data", books);
      return resp;
    }));

    router.get("/api/v1/books/:bookId/content").respond(ctx -> {
      String bookId = ctx.pathParam("bookId");
      JsonObject resp = new JsonObject();

      return BookRepository.getInstance().findById(bookId).map(book -> {
        String parent = "/api/v1/books/" + bookId + "/" + "resources/";
        try {
          EpubParser parser = new EpubParser(new File(book.getPath()));
          fun.lzwi.epubime.epub.EpubBook epubBook = parser.parse();
          List<EpubChapter> chapters = epubBook.getChapters();
          List<EpubChapter> content = chapters.stream().filter(c -> !c.getContent().startsWith("#")).peek((c) -> {
            String h = c.getContent();
            if (h.startsWith("../")) {
              // 去除../
              h = h.substring(3);
            }
            String p1 = h;
            if (h.contains("#")) {
              // 去除#
              h = h.substring(0, h.indexOf("#"));
            }
            // 无#的html文件
            String p2 = URLDecoder.decode(h, StandardCharsets.UTF_8); // 解码url
            String href =
              epubBook.getResources().stream().filter(r -> r.getHref().endsWith(p2)).findFirst().get().getHref();
            if (p1.contains("#")) {
              // 有#的html文件
              href = href + p1.substring(p1.indexOf("#"));
            }
            c.setContent(parent + href);
          }).toList();
          resp.put("code", 200);
          resp.put("msg", "获取书籍目录成功。");
          resp.put("data", content);
        } catch (EpubParseException e) {
          resp.put("code", 400);
          resp.put("msg", e.getMessage());
        }

        return resp;
      });
    });


    router.getWithRegex("/api/v1/books/(?<bookId>[^\\\\/]+)/resources/(?<href>.*)").respond(ctx -> {
      String bookId = ctx.pathParam("bookId");
      String href = ctx.pathParam("href");
      return BookRepository.getInstance().findById(bookId).compose(book -> {
        ctx.response().putHeader("content-type", EpubReader.getResourceType(book.getPath(), href));

        return EpubReader.getResource(book.getPath(), href);
      }).map(b -> {
        String type = ctx.response().headers().get("content-type");
        if (!type.contains("html") && !type.contains("xml")) {
          return b;
        }
        Document document = Jsoup.parse(b.toString());
        document.body().select("img").forEach(img -> {
          String parent = ctx.pathParam("href").substring(0, ctx.pathParam("href").lastIndexOf("/") + 1);
          String src = img.attr("src");
          if (src.startsWith("../")) {
            // 去除../
            src = src.substring(3);
            // 去除最后一个/
            parent = parent.substring(0, parent.length() - 2);
            // 获取父目录
            parent = parent.substring(0, parent.lastIndexOf("/") + 1);
          }
          img.attr("src", "/api/v1/books/%s/resources/%s".formatted(bookId, parent + src));
        });
        document.body().select("image").forEach(img -> {
          String parent = ctx.pathParam("href").substring(0, ctx.pathParam("href").lastIndexOf("/") + 1);
          String src = img.attr("xlink:href");
          if (src.startsWith("../")) {
            // 去除../
            src = src.substring(3);
            // 去除最后一个/
            parent = parent.substring(0, parent.length() - 2);
            // 获取父目录
            parent = parent.substring(0, parent.lastIndexOf("/") + 1);
          }
          img.attr("xlink:href", "/api/v1/books/%s/resources/%s".formatted(bookId, parent + src));
        });
        document.body().select("a").forEach(a -> {
          String parent = ctx.pathParam("href").substring(0, ctx.pathParam("href").lastIndexOf("/") + 1);
          String src = a.attr("href");
          if (src.startsWith("../")) {
            // 去除../
            src = src.substring(3);
            // 去除最后一个/
            parent = parent.substring(0, parent.length() - 2);
            // 获取父目录
            parent = parent.substring(0, parent.lastIndexOf("/") + 1);
          }
          a.attr("href", "/api/v1/books/%s/resources/%s".formatted(bookId, parent + src));
          if (src.startsWith("#") || src.startsWith("http://") || src.startsWith("https://")) {
            a.attr("href", src);
          }
          if (src.isBlank()) {
            a.removeAttr("href");
          }
        });
        // 提取正文
        String body = document.body().html();
        return Buffer.buffer(body);
      });
    });
  }
}
