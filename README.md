# str-obf
用于[stringfog](https://github.com/MegatronKing/StringFog) 实现流程的分析和调试，支持AGP 8.0;

## 项目结构

    app 被加密模块
    buildSrc 字符串加密实现核心插件
    xor 需要的库，可以修改


### buildSrc

插件入口用kotlin写的，整体的实现逻辑:
1. 在prebuild阶段，新增字符串解密的类；
2. 注册一个classVisitor在每个类编译时 ，对字符串加密 



// https://juejin.cn/post/6948626628637360135#heading-14 gradle 插件调试

别说,asm 插桩这个东西还挺有意思, 应该也有java 混淆是通过这个手段实现的。