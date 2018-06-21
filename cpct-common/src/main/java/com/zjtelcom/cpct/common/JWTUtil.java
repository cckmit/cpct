package com.zjtelcom.cpct.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @Author:HuangHua
 * @Descirption: Json Web Token校验类
 * @Date: Created by huanghua on 2018/6/4.
 * @Modified By:
 */
public class JWTUtil {

    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    /**过期时间20分钟**/
    private static final long EXPIRE_TIME = 20*60*1000;

    public static boolean verify(String token,String username, String password){
        /**
         * @Description: 校验token是否正确
         * @author: Huang Hua
         * @param:  [token, username, password]
         * @return: boolean
         * @Date: 2018/6/4
         */
        try{
            //使用静态的字符密文来获取算法器
            Algorithm algorithm = Algorithm.HMAC256(password);

            //通过调用jwt.require()和传递算法实例来创建一个JWTVerifier实例
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        }catch (Exception exception) {
            return false;
        }
    }

    public static String getUsername(String token){
        /**
         * @Description: 获取token中的username信息
         * @author: Huang Hua
         * @param:  [token]
         * @return: java.lang.String
         * @Date: 2018/6/4
         */
        try{
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        }catch (JWTDecodeException e){
            return null;
        }
    }

    public static String sign(String username,String password){
        /**
         * @Description: 生成签名,20min后过期
         * @author: Huang Hua
         * @param:  [username, password]
         * @return: java.lang.String
         * @Date: 2018/6/5
         */
        try{
            Date date = new Date(System.currentTimeMillis()+EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(password);
            // 附带username信息
            return JWT.create()
                    .withClaim("username", username)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
