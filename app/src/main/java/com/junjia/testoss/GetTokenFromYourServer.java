package com.junjia.testoss;


/**
 * @author: zhouzhuo<yecan.xyc@alibaba-inc.com>
 * Apr 2, 2015
 *
 */
public class GetTokenFromYourServer {

    static {
        /*
         * 如果需要采用服务端加签的方式，可以直接在tokenGenerator中向你的业务服务器发起
         * 同步http post请求，把相关字段拼接之后发过去，然后获得加签结果。
         *
         */

        /*
        OSSClient.setGlobalDefaultTokenGenerator(new TokenGenerator() {
            @Override
            public String generateToken(String httpMethod, String md5, String type, String date,
                    String ossHeaders, String resource) {

                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
                        + resource;

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("content", content));
                HttpPost post = new HttpPost("http://110.75.82.106/oss");
                String sign = null;

                try {
                    post.setEntity(new UrlEncodedFormEntity(params));
                    HttpResponse response = new DefaultHttpClient().execute(post);
                    sign = EntityUtils.toString(response.getEntity()).trim();
                } catch (Exception ignore) {
                }
                Log.d("OSS_Test", "[genToken] - remote: " + sign);
                return sign;
            }
        });
         */
        /*
         ********************************************************
         * 以下是加签服务器的代码示例： (nginx + lua脚本)
         *
         * local access_key = "ak";
         * local screct_key = "sk";
         * local sign_str;
         *
         * ngx.req.read_body();
         * --local body = ngx.req.get_body_data();
         *
         * local args, err = ngx.req.get_post_args();
         * for key, val in pairs(args) do
         *         if key == "content" then
         *                 sign_str = val;
         *         end
         * end
         *
         * local sign_result = ngx.encode_base64(ngx.hmac_sha1(screct_key, sign_str));
         *
         * ngx.say("OSS "..access_key..":"..sign_result);
         ********************************************************/
    }
}
