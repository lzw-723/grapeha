package fun.lzwi.grapeha.api;

import fun.lzwi.grapeha.db.repository.BookRepository;
import fun.lzwi.grapeha.db.repository.BookshelfRepository;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.stream.Collectors;

public class BookShelfAPI {
  public static void init(Router router, Vertx vertx) {
    router.get("/api/v1/bookshelves").respond(ctx -> BookshelfRepository.getInstance().findAll().map(shelves -> {
      JsonObject resp = new JsonObject();
      resp.put("code", 200);
      resp.put("msg", "获取书架列表成功。");
      resp.put("data", shelves);
      return resp;
    }));

    router.get("/api/v1/bookshelves/:id").respond(ctx -> BookshelfRepository.getInstance().findById(ctx.pathParam("id")).map(bookshelf -> {
      JsonObject resp = new JsonObject();
      resp.put("code", 200);
      resp.put("msg", "获取书架成功。");
      resp.put("data", bookshelf);
      return resp;
    }));

    router.get("/api/v1/bookshelves/:id/books").respond(ctx -> BookshelfRepository.getInstance().findById(ctx.pathParam("id")).compose(bookshelf -> BookRepository.getInstance().findAll().map(books -> books.stream().filter(book -> book.getPath().startsWith(bookshelf.getPath())).collect(Collectors.toList()))).map(books -> {
      JsonObject resp = new JsonObject();
      resp.put("code", 200);
      resp.put("msg", "获取书架中的书籍列表成功。");
      resp.put("data", books);
      return resp;
    }));
  }
}
