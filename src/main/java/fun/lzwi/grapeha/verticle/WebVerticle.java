package fun.lzwi.grapeha.verticle;

import fun.lzwi.grapeha.api.BookAPI;
import fun.lzwi.grapeha.api.BookShelfAPI;
import fun.lzwi.grapeha.api.UserAPI;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class WebVerticle extends AbstractVerticle {
  Logger logger = LoggerFactory.getLogger(WebVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start(startPromise);

    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    webui(router);
    router.route().handler(CorsHandler.create().allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.POST));
    router.route().handler(BodyHandler.create());
    api(router);
    server.requestHandler(router).listen(8080);
    logger.info("\n\tapp running at http://localhost:8080\n");
  }

  public void webui(Router router) {
    router.route("/*").handler(StaticHandler.create("D:/Projects/Java/grapeha/webui/dist"));
  }

  public void api(Router router) {
    router.get("/api/v1/system").respond(ctx -> Future.succeededFuture(new JsonObject().put("hello", "world")));

    BookAPI.init(router, vertx);
    UserAPI.init(router, vertx);
    BookShelfAPI.init(router, vertx);

  }


}
