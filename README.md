# android 设备控制工具
- gui采用javafx编写，通过adb转发socket发送命令控制设备

## TODO
其他待研究项目  
1.https://blog.csdn.net/qq_37217804/article/details/79639083  
2.https://juejin.cn/post/7094518174690836510   
3.https://github.com/vidstige/jadb
4.将adb打包进来，自动搜索adb路径
5.自动升级jar包

```java
ProcessBuilder pb = new ProcessBuilder("adb", "-s", "0123456789ABCDEF", "push", inputfile, outputfile);
Process pc = pb.start();
pc.waitFor();
System.out.println("Done");
```