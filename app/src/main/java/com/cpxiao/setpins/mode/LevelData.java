package com.cpxiao.setpins.mode;

import java.util.Random;

/**
 * LevelData
 *
 * @author cpxiao on 2017/6/6.
 */
public class LevelData {
    private static final Random mRandom = new Random();

    /**
     * 关卡
     */
    public int level;
    /**
     * 旋转速度
     */
    public int rotateSpeed;
    /**
     * 基础针数量
     */
    public int baseCirclesNumber;
    /**
     * 待插入针数量
     */
    public int playerCirclesNumber;


    private LevelData() {

    }

    /**
     * 随机关卡,最大基础10，最大待插18
     *
     * @param score int
     * @return LevelData
     */
    public static LevelData getRandomData(int score) {
        LevelData data = new LevelData();
        data.level = score;
        int tmpSpeed = 70 + mRandom.nextInt(40);
        data.rotateSpeed = mRandom.nextBoolean() ? tmpSpeed : -tmpSpeed;
        data.baseCirclesNumber = Math.min((3 + mRandom.nextInt(score + 3)), 10);
        data.playerCirclesNumber = Math.min((3 + mRandom.nextInt(score + 5)), 18);
        return data;
    }

}
