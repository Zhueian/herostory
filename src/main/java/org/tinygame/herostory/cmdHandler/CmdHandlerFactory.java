package org.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.util.PackageUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiucy on 2020/8/12.
 */
public final class CmdHandlerFactory {
    private CmdHandlerFactory(){}
    static private final Logger LOGGER = LoggerFactory.getLogger(CmdHandlerFactory.class);

    /**
     * 处理器字典
     */
    static private Map<Class<?>,ICmdHandler<? extends GeneratedMessageV3 >> _handlerMap = new HashMap<>();

    static public void init(){
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(
                CmdHandlerFactory.class.getPackage().getName(),
                true,
                ICmdHandler.class);
        System.out.println("packetgeName = "+CmdHandlerFactory.class.getPackage().getName());
        System.out.println("clazzSet.size() = "+clazzSet.size());
        for (Class<?> clazz : clazzSet) {
            //如果是抽象类就别往下走了，把接口过滤掉，ICmdHandler
            if ((clazz.getModifiers() & Modifier.ABSTRACT) != 0){
                continue;
            }
            Method[] methodArray = clazz.getDeclaredMethods();
            // 定义消息类型
            Class<?> msgType = null;
            for (Method currMethod : methodArray) {
                // 不是handle() 函数 跳过
                if (!currMethod.getName().equals("handle")){
                    continue;
                }
                //拿到handle()函数的入参类型
                Class<?>[] paramTypeArray = currMethod.getParameterTypes();
                // 防止有人重载 handle()
                if (paramTypeArray.length < 2
                        || !GeneratedMessageV3.class.isAssignableFrom(paramTypeArray[1])){
                    continue;
                }
                msgType = paramTypeArray[1];
                break;
            }
            if (null == msgType){
                continue;
            }
            try {
                ICmdHandler<?> newHandler = (ICmdHandler<?>)clazz.newInstance();
                LOGGER.info("特定消息和特定处理器关联上了 {} <==> {}",msgType.getName(),clazz.getName());
                _handlerMap.put(msgType,newHandler);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(),e);
            }

        }
    }
    static public ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz){
        if (null == msgClazz) {
            return null;
        }
        return _handlerMap.get(msgClazz);
    }
}
