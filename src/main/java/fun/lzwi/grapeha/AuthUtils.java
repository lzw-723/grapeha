package fun.lzwi.grapeha;

import fun.lzwi.grapeha.library.User;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

public class AuthUtils {
  public String generateToken(Vertx vertx, User user) {
    JWTAuth provider = JWTAuth.create(vertx, new JWTAuthOptions()
        .addPubSecKey(new PubSecKeyOptions()
            .setAlgorithm("HS256")
            .setBuffer(user.getPassword())));

    String token = provider.generateToken(new JsonObject());
    return token;
  }
}
