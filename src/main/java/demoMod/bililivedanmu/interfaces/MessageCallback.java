package demoMod.bililivedanmu.interfaces;

public interface MessageCallback {
    void onDanmu(String user, String msg);

    void onGift(String user, String giftName, String action,int amount);

    void watchedChange(int num);

    void enterRoom(String username);
}
