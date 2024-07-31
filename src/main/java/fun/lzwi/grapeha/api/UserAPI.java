package fun.lzwi.grapeha.api;

import fun.lzwi.grapeha.AuthUtils;
import fun.lzwi.grapeha.db.repository.UserRepository;
import fun.lzwi.grapeha.library.User;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.JWTAuthHandler;

public class UserAPI {
  public static void init(Router router, Vertx vertx) {
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

    // 验证token是否有效
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
