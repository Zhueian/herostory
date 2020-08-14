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
        _msgCodeAndMsgBodyMap.put(GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE,GameMsgProtocol.UserEntryCmd.getDefaultInstance());
        _msgCodeAndMsgBodyMap.put(GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE,GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance());
        _msgCodeAndMsgBodyMap.put(GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE,GameMsgProtocol.UserMoveToCmd.getDefaultInstance());

        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.UserEntryResult.class,GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE);
        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.WhoElseIsHereResult.class,GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE);
        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.UserMoveToResult.class,GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE);
        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.UserQuitResult.class,GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE);
    }

    static public Message.Builder getBuilderByMsgCode(int msgCode){
        if (msgCode < 0) return null;
        GeneratedMessageV3 msg = _msgCodeAndMsgBodyMap.get(msgCode);
        if (null == msg) return null;
        return msg.newBuilderForType();
    }

    static public int getMsgCodeByMsgClazz(Class<?> msgClazz){
        if(null == msgClazz) return -1;
        Integer msgCode = _msgClazzAndMsgCodeMap.get(msgClazz);
        if (null != msgClazz) return msgCode.intValue();
        return -1;
    }
}
