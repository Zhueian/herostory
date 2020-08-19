package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiucy on 2020/8/12.
 * 用户管理器
 */
public final class UserManager {
    private UserManager(){}
    /**
     * 用户字典
     */
    static private final Map<Integer,User> _userMap = new HashMap<>();

    /**
     * 添加用户
     * @param user
     */
    static public void addUser(User user){
        if (null == user) return;
        _userMap.put(user.userId,user);
    }

    /**
     * 移除用户
     * @param userId
     */
    static public void removeUserByUserId(int userId){
        _userMap.remove(userId);
    }

    /**
     * 用户列表
     * @return
     */
    static public Collection<User> listUser(){
        return _userMap.values();
    }

    public static User getUserById(int userId) {
        return _userMap.get(userId);
    }
}
