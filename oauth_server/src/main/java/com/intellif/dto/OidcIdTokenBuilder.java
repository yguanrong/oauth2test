package com.intellif.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultHeader;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import io.jsonwebtoken.impl.TextCodec;
import io.jsonwebtoken.impl.crypto.DefaultJwtSigner;
import io.jsonwebtoken.impl.crypto.JwtSigner;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.lang.Collections;
import io.jsonwebtoken.lang.Objects;
import io.jsonwebtoken.lang.Strings;
import lombok.Data;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;


/**
 * id_token信息的实体类
 * Created by YangGuanRong
 * date: 2023/2/6
 */
@Data
public class OidcIdTokenBuilder implements JwtBuilder {

    public static OidcIdTokenBuilder builder() {
        return new OidcIdTokenBuilder();
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private Header header;
    private Claims claims;
    private String payload;
    private SignatureAlgorithm algorithm;
    private Key key;
    private byte[] keyBytes;
    private CompressionCodec compressionCodec;

    public OidcIdTokenBuilder() {
    }

    // ***********************ID Token start***************
    /**
     * 设置 令牌颁发者
     *
     * @param iss
     * @return
     */
    public OidcIdTokenBuilder setIssuer(String iss) {
        if (Strings.hasText(iss)) {
            this.ensureClaims().setIssuer(iss);
        } else if (this.claims != null) {
            this.claims.setIssuer(iss);
        }

        return this;
    }

    /**
     * 令牌颁发时间
     *
     * @param iat
     * @return
     */
    public OidcIdTokenBuilder setIssuedAt(Date iat) {
        if (iat != null) {
            this.ensureClaims().setIssuedAt(iat);
        } else if (this.claims != null) {
            this.claims.setIssuedAt(iat);
        }

        return this;
    }

    /**
     * 令牌过期时间
     *
     * @param exp
     * @return
     */
    public OidcIdTokenBuilder setExpiration(Date exp) {
        if (exp != null) {
            this.ensureClaims().setExpiration(exp);
        } else if (this.claims != null) {
            this.claims.setExpiration(exp);
        }

        return this;
    }

    /**
     * 用户id
     *
     * @param sub
     * @return
     */
    public OidcIdTokenBuilder setSubject(String sub) {
        if (Strings.hasText(sub)) {
            this.ensureClaims().setSubject(sub);
        } else if (this.claims != null) {
            this.claims.setSubject(sub);
        }

        return this;
    }

    /**
     * 用户姓名
     *
     * @param name
     * @return
     */
    public OidcIdTokenBuilder setName(String name) {
        return (OidcIdTokenBuilder)this.claim("name", name);
    }

    /**
     * 用户登录名
     *
     * @param loginName
     * @return
     */
    public OidcIdTokenBuilder setLoginName(String loginName) {
        return (OidcIdTokenBuilder)this.claim("login_name", loginName);
    }

    /**
     * 用户头像
     *
     * @param picture
     * @return
     */
    public OidcIdTokenBuilder setPicture(String picture) {
        return (OidcIdTokenBuilder)this.claim("picture", picture);
    }

    /**
     * 令牌接收者，OAuth应用ID
     *
     * @param clientId
     * @return
     */
    public OidcIdTokenBuilder setAudience(String clientId) {
        if (Strings.hasText(clientId)) {
            this.ensureClaims().setAudience(clientId);
        } else if (this.claims != null) {
            this.claims.setAudience(clientId);
        }

        return this;
    }

    /**
     * 随机字符串，用来防止重放攻击
     *
     * @param nonce
     * @return
     */
    public OidcIdTokenBuilder setNonce(String nonce) {
        return (OidcIdTokenBuilder)this.claim("nonce", nonce);
    }

    /**
     * 生成jwt
     * @return
     */
    public String build(){
        return this.compact();
    }

    // ***********************ID Token end***********************************************

    public JwtBuilder claim(String name, Object value) {
        Assert.hasText(name, "Claim property name cannot be null or empty.");
        if (this.claims == null) {
            if (value != null) {
                this.ensureClaims().put(name, value);
            }
        } else if (value == null) {
            this.claims.remove(name);
        } else {
            this.claims.put(name, value);
        }

        return this;
    }


    public JwtBuilder setNotBefore(Date nbf) {
        if (nbf != null) {
            this.ensureClaims().setNotBefore(nbf);
        } else if (this.claims != null) {
            this.claims.setNotBefore(nbf);
        }

        return this;
    }

    public JwtBuilder setId(String jti) {
        if (Strings.hasText(jti)) {
            this.ensureClaims().setId(jti);
        } else if (this.claims != null) {
            this.claims.setId(jti);
        }

        return this;
    }

    public JwtBuilder setHeader(Header header) {
        this.header = header;
        return this;
    }

    public JwtBuilder setHeader(Map<String, Object> header) {
        this.header = new DefaultHeader(header);
        return this;
    }

    public JwtBuilder setHeaderParams(Map<String, Object> params) {
        if (!Collections.isEmpty(params)) {
            Header header = this.ensureHeader();
            Iterator var3 = params.entrySet().iterator();

            while (var3.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry) var3.next();
                header.put(entry.getKey(), entry.getValue());
            }
        }

        return this;
    }

    protected Header ensureHeader() {
        if (this.header == null) {
            this.header = new DefaultHeader();
        }

        return this.header;
    }

    public JwtBuilder setHeaderParam(String name, Object value) {
        this.ensureHeader().put(name, value);
        return this;
    }

    public JwtBuilder signWith(SignatureAlgorithm alg, byte[] secretKey) {
        Assert.notNull(alg, "SignatureAlgorithm cannot be null.");
        Assert.notEmpty(secretKey, "secret key byte array cannot be null or empty.");
        Assert.isTrue(alg.isHmac(), "Key bytes may only be specified for HMAC signatures.  If using RSA or Elliptic Curve, use the signWith(SignatureAlgorithm, Key) method instead.");
        this.algorithm = alg;
        this.keyBytes = secretKey;
        return this;
    }

    public OidcIdTokenBuilder signWith(SignatureAlgorithm alg, String base64EncodedSecretKey) {
        Assert.hasText(base64EncodedSecretKey, "base64-encoded secret key cannot be null or empty.");
        Assert.isTrue(alg.isHmac(), "Base64-encoded key bytes may only be specified for HMAC signatures.  If using RSA or Elliptic Curve, use the signWith(SignatureAlgorithm, Key) method instead.");
        byte[] bytes = TextCodec.BASE64.decode(base64EncodedSecretKey);
        return (OidcIdTokenBuilder)this.signWith(alg, bytes);
    }

    public JwtBuilder signWith(SignatureAlgorithm alg, Key key) {
        Assert.notNull(alg, "SignatureAlgorithm cannot be null.");
        Assert.notNull(key, "Key argument cannot be null.");
        this.algorithm = alg;
        this.key = key;
        return this;
    }

    public JwtBuilder compressWith(CompressionCodec compressionCodec) {
        Assert.notNull(compressionCodec, "compressionCodec cannot be null");
        this.compressionCodec = compressionCodec;
        return this;
    }

    public JwtBuilder setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    protected Claims ensureClaims() {
        if (this.claims == null) {
            this.claims = new DefaultClaims();
        }

        return this.claims;
    }

    public JwtBuilder setClaims(Claims claims) {
        this.claims = claims;
        return this;
    }

    public JwtBuilder setClaims(Map<String, Object> claims) {
        this.claims = Jwts.claims(claims);
        return this;
    }

    public JwtBuilder addClaims(Map<String, Object> claims) {
        this.ensureClaims().putAll(claims);
        return this;
    }


    public String compact() {
        if (this.payload == null && Collections.isEmpty(this.claims)) {
            throw new IllegalStateException("Either 'payload' or 'claims' must be specified.");
        } else if (this.payload != null && !Collections.isEmpty(this.claims)) {
            throw new IllegalStateException("Both 'payload' and 'claims' cannot both be specified. Choose either one.");
        } else if (this.key != null && this.keyBytes != null) {
            throw new IllegalStateException("A key object and key bytes cannot both be specified. Choose either one.");
        } else {
            Header header = this.ensureHeader();
            Key key = this.key;
            if (key == null && !Objects.isEmpty(this.keyBytes)) {
                key = new SecretKeySpec(this.keyBytes, this.algorithm.getJcaName());
            }

            Object jwsHeader;
            if (header instanceof JwsHeader) {
                jwsHeader = (JwsHeader) header;
            } else {
                jwsHeader = new DefaultJwsHeader(header);
            }

            if (key != null) {
                ((JwsHeader) jwsHeader).setAlgorithm(this.algorithm.getValue());
            } else {
                ((JwsHeader) jwsHeader).setAlgorithm(SignatureAlgorithm.NONE.getValue());
            }

            if (this.compressionCodec != null) {
                ((JwsHeader) jwsHeader).setCompressionAlgorithm(this.compressionCodec.getAlgorithmName());
            }

            String base64UrlEncodedHeader = this.base64UrlEncode(jwsHeader, "Unable to serialize header to json.");
            String base64UrlEncodedBody;
            if (this.compressionCodec != null) {
                byte[] bytes;
                try {
                    bytes = this.payload != null ? this.payload.getBytes(Strings.UTF_8) : this.toJson(this.claims);
                } catch (JsonProcessingException var9) {
                    throw new IllegalArgumentException("Unable to serialize claims object to json.");
                }

                base64UrlEncodedBody = TextCodec.BASE64URL.encode(this.compressionCodec.compress(bytes));
            } else {
                base64UrlEncodedBody = this.payload != null ? TextCodec.BASE64URL.encode(this.payload) : this.base64UrlEncode(this.claims, "Unable to serialize claims object to json.");
            }

            String jwt = base64UrlEncodedHeader + '.' + base64UrlEncodedBody;
            if (key != null) {
                JwtSigner signer = this.createSigner(this.algorithm, (Key) key);
                String base64UrlSignature = signer.sign(jwt);
                jwt = jwt + '.' + base64UrlSignature;
            } else {
                jwt = jwt + '.';
            }

            return jwt;
        }
    }

    protected JwtSigner createSigner(SignatureAlgorithm alg, Key key) {
        return new DefaultJwtSigner(alg, key);
    }

    protected String base64UrlEncode(Object o, String errMsg) {
        byte[] bytes;
        try {
            bytes = this.toJson(o);
        } catch (JsonProcessingException var5) {
            throw new IllegalStateException(errMsg, var5);
        }

        return TextCodec.BASE64URL.encode(bytes);
    }

    protected byte[] toJson(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }
}