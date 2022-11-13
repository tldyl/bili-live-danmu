package demoMod.bililivedanmu.interfaces;

import com.badlogic.gdx.graphics.Color;

public interface MessageCallback {
    void onDanmu(String user, String msg, Color dmColor);

    void onGift(String user, String giftName, String action,int amount);

    void watchedChange(int num);

    void enterRoom(String username);
}
