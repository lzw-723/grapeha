package fun.lzwi.grapeha;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import fun.lzwi.grapeha.library.User;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

public class AuthUtils {

  public static JWTAuth getProvider(Vertx vertx) {
    JWTAuth provider = JWTAuth.create(vertx, new JWTAuthOptions().addPubSecKey(new PubSecKeyOptions().setAlgorithm("HS256").setBuffer("keyboard cat")));
    return provider;
  }

  public static String generateToken(Vertx vertx, User user) {
    String token = getProvider(vertx).generateToken(new JsonObject().put("iss", AuthUtils.class.getSimpleName()).put("aud", user.getUsername()).put("iat", LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()).put("nbf", LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()).put("exp", LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()));
    return token;
  }

  public static Future<Boolean> verifyToken(Vertx vertx, String token, User user) {
    return getProvider(vertx).authenticate(new TokenCredentials(token)).map(u -> !u.expired() && Objects.equals(u.principal().getString("aud"), user.getUsername()));
  }
}
