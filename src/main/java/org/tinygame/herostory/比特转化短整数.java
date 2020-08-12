package org.tinygame.herostory;

/**
 * Created by qiucy on 2020/8/8.
 */
public class 比特转化短整数 {
    public static void main(String[] args) {
        byte a = (byte)1;
        byte b = (byte)2;
        //a 左移8位 与 b；
        short i = (short)(a << 8 | b);
    }
}
