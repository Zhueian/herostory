package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiucy on 2020/8/14.
 * 消息识别器 本质还是工厂模式
 */
public class GameMsgRecognizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);
    private GameMsgRecognizer(){}

    /**
     * 消息代码和消息体字典
     */
    static private final Map<Integer,GeneratedMessageV3> _msgCodeAndMsgBodyMap = new HashMap<>();
    /**
     * 消息类型和消息标号字典
     */
    static private final Map<Class<?>,Integer> _msgClazzAndMsgCodeMap = new HashMap<>();

    static public void init(){
        // 获取所有内部类，子类
        Class<?>[] declaredClasses = GameMsgProtocol.class.getDeclaredClasses();
        //TODO stream改造
        for (Class<?> innerClazz : declaredClasses) {
            if (!GeneratedMessageV3.class.isAssignableFrom(innerClazz)){
                continue;
            }
            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();
            System.out.println("clazzName.toLowerCase = "+clazzName);
            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                String strMsgCode = msgCode.name();
                strMsgCode = strMsgCode.replaceAll("_","");
                strMsgCode = strMsgCode.toLowerCase();
                // 防御式编程
                if (!strMsgCode.startsWith(clazzName)){
                    continue;
                }
                try{
                    // getDefaultInstance静态方法 入参自己
                    Object retrunObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);
                    LOGGER.info("{} <==> {}",innerClazz.getName(),msgCode.getNumber());
                    _msgCodeAndMsgBodyMap.put(msgCode.getNumber(), (GeneratedMessageV3) retrunObj);
                    _msgClazzAndMsgCodeMap.put(innerClazz,msgCode.getNumber());

                }catch (Exception e){
                    LOGGER.error(e.getMessage(),e);
                }
            }
        }
    }

    static public Message.Builder getBuilderByMsgCode(int msgCode){
        if (msgCode < 0) {
            return null;
        }
        GeneratedMessageV3 msg = _msgCodeAndMsgBodyMap.get(msgCode);
        if (null == msg) {
            return null;
        }
        return msg.newBuilderForType();
    }

    static public int getMsgCodeByMsgClazz(Class<?> msgClazz){
        if(null == msgClazz) {
            return -1;
        }
        Integer msgCode = _msgClazzAndMsgCodeMap.get(msgClazz);
        if (null != msgCode) {
            return msgCode.intValue();
        }
        return -1;
    }
}
