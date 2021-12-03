set(LUA_SRC
        # 虚拟机核心功能部分
        src/lua.c # lua的可执行入口main函数
        src/lapi.c # c语言接口
        src/ldebug.c # debug接口
        src/ldo.c # 函数调用以及栈管理
        src/lfunc.c # 函数原型及闭包管理
        src/lgc.c # 垃圾回收机制
        src/lmem.c # 内存管理接口
        src/lobject.c # 对象操作函数
        src/lopcodes.c # 虚拟机字节码定义
        src/lstate.c # 全局状态机 管理全局信息
        src/lstring.c # 字符串池
        src/ltable.c # 表类型的相关操作
        src/ltm.c # 元方法
        src/lvm.c # 虚拟机
        src/lzio.c # 输入流接口
        # 源代码解析和预编译
        src/lcode.c # 代码生成器
        src/ldump.c # 序列化预编译的lua字节码
        src/llex.c # 词法分析器
        src/lparser.c # 解析器
        src/lundump.c # 还原预编译的字节码
        # 内嵌库
        src/lauxlib.c # 库编写用到的辅助函数库
        src/lbaselib.c # 基础库
        src/ldblib.c # Debug库
        src/linit.c # 内嵌库的初始化
        src/liolib.c # IO库
        src/lmathlib.c # 数学库
        src/loadlib.c # 动态扩展库管理
        src/loslib.c # OS库
        src/lstrlib.c # 字符串库
        src/ltablib.c # 表处理库
        src/lua_bit.c
        src/lua_cjson.c
        src/lua_cmsgpack.c
        src/lua_struct.c
        src/luac.c
        src/print.c
        src/strbuf.c
        src/fpconv.c)

add_library(lua STATIC ${LUA_SRC})