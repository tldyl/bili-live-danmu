package demoMod.bililivedanmu.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import demoMod.bililivedanmu.BiliLiveDanmu;
import demoMod.bililivedanmu.client.BaseWebSocketClient;
import demoMod.bililivedanmu.interfaces.MessageCallback;
import demoMod.bililivedanmu.ui.panels.RoomNumPanel;
import demoMod.bililivedanmu.utils.HttpURLConnectionUtil;
import demoMod.bililivedanmu.vfx.MessageBox;

import java.net.URI;
import java.net.URISyntaxException;

public class Danmu extends CustomRelic implements ClickableRelic, MessageCallback {
    public static final String ID = BiliLiveDanmu.makeID("Danmu");
    public static final String IMG_PATH = "relics/danmu.png";
    private static BaseWebSocketClient client;
    private RoomNumPanel panel;
    private static MessageBox messageBox;

    public Danmu() {
        super(ID, new Texture(BiliLiveDanmu.getResourcePath(IMG_PATH)),
                RelicTier.SPECIAL, LandingSound.CLINK);
        this.panel = new RoomNumPanel();
        this.panel.relicRef = this;
        if (messageBox == null) {
            messageBox = new MessageBox();
        }
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public void setRoomId(int roomNum) {
        int roomId = HttpURLConnectionUtil.getLiveRoomId(roomNum);
        if (client != null) {
            client.close();
        }
        try {
            Danmu.client = new BaseWebSocketClient(new URI("wss://" + HttpURLConnectionUtil.getHostAddr(roomId)), roomId);
            Danmu.client.connect();
            Danmu.client.setMessageCallback(this);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        super.update();
        this.panel.update();
        messageBox.update();
    }

    @Override
    public void renderInTopPanel(SpriteBatch sb) {
        super.renderInTopPanel(sb);
        this.panel.render(sb);
        messageBox.render(sb);
    }

    @Override
    public void onRightClick() {
        this.panel.show(MainMenuScreen.CurScreen.NONE);
    }

    @Override
    public void onDanmu(String user, String msg, Color dmColor) {
        messageBox.addDanmu(user + ":", msg, dmColor);
    }

    @Override
    public void onGift(String user, String giftName, String action, int amount) {
        messageBox.addDanmu(user, action + giftName + "x" + amount);
    }

    @Override
    public void watchedChange(int num) {
        messageBox.setPopularity(num);
    }

    @Override
    public void enterRoom(String username) {
        messageBox.addDanmu(username, this.DESCRIPTIONS[1]);
    }
}
