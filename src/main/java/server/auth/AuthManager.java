package server.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import server.exception.ProtocolException;

public class AuthManager {
    public static final int DAY = 24 * 60 * 60 * 1000;
    public static final int EXPIRY = 7 * DAY;
    public static final int AGE = 5 * DAY;

    public static AuthManager authManager;

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    private AuthManager(String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).withIssuer("auth0").build();
    }

    public static void initial(String secret) {
        authManager = new AuthManager(secret);
    }

    public static AuthManager getInstance() {
        return authManager;
    }

    public String getToken(String email) {
        return JWT.create()
                .withIssuer("auth0")
                .withClaim("email", email)
                .withClaim("issuedAt", System.currentTimeMillis())
                .sign(algorithm);
    }

    public String verifyToken(String token) throws ProtocolException {
        try {
            DecodedJWT jwt = verifier.verify(token);
//            if (System.currentTimeMillis() + EXPIRY > jwt.getClaim("issuedAt").asLong())
//                throw new ProtocolException(ProtocolException.TOKEN_EXPIRED); // TOOD
            return jwt.getClaim("email").asString();
        } catch (JWTVerificationException exception){
            throw new ProtocolException(ProtocolException.TOKEN_NOT_VALIDATED);
        }
    }
}
