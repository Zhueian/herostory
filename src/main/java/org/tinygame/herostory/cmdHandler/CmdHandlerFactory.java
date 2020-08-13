package org.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiucy on 2020/8/12.
 */
public final class CmdHandlerFactory {
    private CmdHandlerFactory(){}

    /**
     * 处理器字典
     */
    static private Map<Class<?>,ICmdHandler<? extends GeneratedMessageV3 >> _handlerMap = new HashMap<>();

    static public void init(){
        _handlerMap.put(GameMsgProtocol.UserEntryCmd.class,new UserEntryCmdHandler());
        _handlerMap.put(GameMsgProtocol.WhoElseIsHereCmd.class,new WhoElseIsHereCmdHandler());
        _handlerMap.put(GameMsgProtocol.UserMoveToCmd.class,new UserMoveToCmdHandler());
    }
    static public ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz){
        if (null == msgClazz) {
            return null;
        }
        return _handlerMap.get(msgClazz);
    }
}
