# yaml生成代码
1. https://openapi-generator.tech/#try 去这个网站安装openapi-generator

2. 环境：MacOS， 运行命令（注意一下 -i ， -c 跟 -o 后面的路径设置成自己的，yaml跟config在/doc文件夹下） openapi-generator generate -i ~/Documents/vs\ code/test.yaml -g spring -c ~/Documents/vs\ code/apiConfig.json -o ~/Downloads/openApi

# 本项目中
1. pull下，然后在mysql中创建数据库名：openapi，然后可直接运行 http://localhost:8080/api/swagger-ui.html访问API文档

2. 测试顺序应该是/user/addUser（注册用户） -> /user/auth（获取token） -> 将token复制粘贴设置到文档右上角的Authorize中 -> /user/{userId}（获取用户信息）

3. 在/user/addUser时，输入的参数会有一个校验，具体在/doc/test.yaml中的components.components.User里面，可以输入与他校验规则相反的看看响应参数