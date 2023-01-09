# oauth2test
oauth2 demo

# 授权码模式
1、需要前端发起重定向请求

http://localhost:9001/oauth/authorize?response_type=code&client_id=admin&redirect_uri=http://www.baidu.com&scope=all

其中：
response_type 固定是code

client_id 是客户端

redirect_uri 重定向的url

![image](https://user-images.githubusercontent.com/41421888/211302952-b69c8235-c892-4b87-b9e0-0fe3361a59a5.png)

会返回一个code
https://www.baidu.com/?code=rGVqdU

使用postMan或者apifox调用获取token：/oauth/token

curl --location --request POST 'http://localhost:9001/oauth/token' \
--header 'User-Agent: Apifox/1.0.0 (https://www.apifox.cn)' \
--header 'Authorization: Basic YWRtaW46MTEyMjMz' \
--data-urlencode 'grant_type=authorization_code' \
--data-urlencode 'code=CJ6pdi' \
--data-urlencode 'redirect_uri=http://www.baidu.com' \
--data-urlencode 'client_id=admin' \
--data-urlencode 'scope=all'


![image](https://user-images.githubusercontent.com/41421888/211306550-eeaf46be-6a53-4e6b-bb16-19d12d804daa.png)
