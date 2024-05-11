package fun.lzwi.grapeha;

import java.util.Random;

import fun.lzwi.grapeha.db.repository.BookRepository;
import fun.lzwi.grapeha.db.repository.UserRepository;
import fun.lzwi.grapeha.library.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
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
    router.route("/api/v1/books").handler(JWTAuthHandler.create(AuthUtils.getProvider(vertx)));
    router.get("/api/v1/books/:bookId").respond(ctx -> {
      String bookId = ctx.pathParam("bookId");
      return BookRepository.getInstance().findById(bookId).map(book -> new JsonObject().put("code", 200).put("msg", "获取书籍成功。").put("data", book));
    });

    router.get("/api/v1/books/:bookId/cover").produces("image/jpeg").produces("image/png").handler(ctx -> {
      String bookId = ctx.pathParam("bookId");
      BookRepository.getInstance().findById(bookId).map(book -> book.getPath().hashCode()).onFailure(e -> {
        HttpServerResponse response = ctx.response();
        response.end("fail");
        logger.error("获取封面失败！", e);
      }).map(code -> String.format("./config/cache/%s.jpg", code)).compose(cover -> vertx.fileSystem().readFile(cover)).onSuccess(buffer -> {
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

    router.get("/api/v1/users").respond(ctx -> UserRepository.getInstance().findAll().map(users -> {
      JsonObject resp = new JsonObject();
      resp.put("code", 200);
      resp.put("msg", "获取用户列表成功。");
      resp.put("data", users);
      return resp;
    }));

    router.post("/api/v1/users").respond(ctx -> {
      String username = ctx.body().asJsonObject().getString("username");
      String password = ctx.body().asJsonObject().getString("password");
      User user = new User();
      user.setUsername(username);
      user.setPassword(password);
      return UserRepository.getInstance().save(user).map(v -> {
        JsonObject resp = new JsonObject();
        resp.put("code", 201);
        resp.put("msg", "用户注册成功。");
        resp.put("data", user);

        return resp;
      });
    });

//    验证token是否有效
    router.get("/api/v1/users/:username").handler(JWTAuthHandler.create(AuthUtils.getProvider(vertx))).respond(ctx -> {
      String username = ctx.pathParam("username");
      return UserRepository.getInstance().findByUsername(username).map(user -> {
        JsonObject resp = new JsonObject();
        resp.put("code", 200);
        resp.put("msg", "获取用户成功。");
        resp.put("data", user);
        return resp;
      });
    });

// 登录（获取/刷新token）
    Route login = router.post("/api/v1/users/:username/token");
    login.handler(ctx -> {
      String username = ctx.pathParam("username");
      UserRepository.getInstance().findByUsername(username).onSuccess(user -> {
        JsonObject resp = new JsonObject();
        if (ctx.body().asJsonObject() == null) {
          ctx.next();
          return;
        }
        String password = ctx.body().asJsonObject().getString("password");
        if (password.equals(user.getPassword())) {
          resp.put("code", 200);
          resp.put("msg", "获取用户Token成功。");
          resp.put("data", AuthUtils.generateToken(vertx, user));
        } else {
          resp.put("code", 400);
          resp.put("msg", "用户Token失效！");
        }
        ctx.json(resp);
      });
    });
    login.respond(ctx -> {
      String username = ctx.pathParam("username");
      return UserRepository.getInstance().findByUsername(username).compose(user -> {
        JsonObject resp = new JsonObject();
        String token = ctx.request().headers().get(HttpHeaders.AUTHORIZATION).split(" ", 2)[1];
        return AuthUtils.verifyToken(vertx, token, user).onFailure(e -> {
          resp.put("code", 400);
          resp.put("msg", "用户Token失效！");
          ctx.json(resp);
        }).map(r -> {
          if (r) {
            resp.put("code", 200);
            resp.put("msg", "刷新用户Token成功。");
            resp.put("data", token);
          } else {
            resp.put("code", 400);
            resp.put("msg", "刷新用户Token失败！");
          }
          return resp;
        });
      });
    });

  }

}
