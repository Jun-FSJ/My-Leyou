# My-Leyou
微服务电商项目

启动乐优项目，分三个大项目，分别为leyou(分别起开对应的微服务就行),
leyou-portal(在idea控制台输入啊live-server --port=9002),
leyou-manage-web(在package.json有start启动器启动)三个项目、

细节:起开leyou-nginx-fastDFS这个虚拟机,
切换用户 su - leyou, 
cd elasticsearch/bin, 
./elasticsearch 打开搜索功能 
nginx+fastDFS自启动了。消息中间件也自启动了 用http://192.168.228.144:15672/#/可以访问

在E:\Java\idea-workspace\myleyou\tools\nginx-1.14.0 cmd 输入start nginx 进行nginx的反向代理，在host文件修改本地ip映射
kibana-6.3.0-windows-x86_64 这个是elasticsearch 的可操作界面，
可以进入E:\Java\idea-workspace\myleyou\tools\kibana-6.3.0-windows-x86_64\bin 运行.bat文件，可访问可操作界面
