package fun.lzwi.grapeha;

import fun.lzwi.grapeha.db.repository.BookRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class WebVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start(startPromise);
    Logger logger = LoggerFactory.getLogger(WebVerticle.class);

    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route("/*").handler(StaticHandler.create("D:/Projects/Java/grapeha/front/dist"));
    router.route().handler(CorsHandler.create().allowedMethod(HttpMethod.GET));
    router.get("/api/v1/system").respond(ctx -> Future.succeededFuture(new JsonObject().put("hello", "world")));
    router.get("/api/v1/books/:bookId").respond(ctx -> {
      String bookId = ctx.pathParam("bookId");
      return BookRepository.getInstance().findById(bookId)
          .map(book -> new JsonObject().put("code", 200).put("msg", "获取书籍成功。").put("data", book));
    });
    router.get("/api/v1/books/:bookId/cover").produces("image/jpeg").produces("image/png").handler(ctx -> {
      String bookId = ctx.pathParam("bookId");
      BookRepository.getInstance().findById(bookId).map(book -> book.getPath().hashCode()).onFailure(e -> {
        HttpServerResponse response = ctx.response();
        response.end("fail");
        logger.error("获取封面失败！", e);
      })
          .map(code -> String.format("./config/cache/%s.jpg", code))
          .compose(cover -> vertx.fileSystem().readFile(cover)).onSuccess(buffer -> {
            HttpServerResponse response = ctx.response();
            String acceptableContentType = ctx.getAcceptableContentType();
            response.putHeader("content-type", acceptableContentType);
            response.end(buffer);
          });
    });

    router.get("/api/v1/books").respond(ctx -> BookRepository.getInstance().findAll().map(books -> {
      JsonObject resp = new JsonObject();
      resp.put("code", 200);
      resp.put("msg", "获取书籍列表成功。");
      resp.put("data", books);
      return resp;
    }));

    server.requestHandler(router).listen(8080);
    logger.info("\n\tapp running at http://localhost:8080\n");
  }

}
