package demoMod.bililivedanmu.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.bililivedanmu.BiliLiveDanmu;
import demoMod.bililivedanmu.interfaces.MessageCallback;
import demoMod.bililivedanmu.utils.ByteBufferInputStream;
import demoMod.bililivedanmu.utils.ZLibUtils;
import demoMod.bililivedanmu.vfx.DanmuEffect;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

public class BaseWebSocketClient extends WebSocketClient {
    private Timer heartBeat;
    private final int roomId;
    private MessageCallback messageCallback;

    public BaseWebSocketClient(URI serverUri, int roomId) {
        super(serverUri);
        System.out.println(serverUri);
        this.roomId = roomId;
    }

    public void setMessageCallback(MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("有新连接建立");
        JSONObject json = new JSONObject();
        json.put("uid", 0);
        json.put("roomid", this.roomId);
        json.put("protover", 1);
        json.put("platform", "web");
        json.put("clientver", "1.4.0");
        this.send(getCertification(json.toJSONString()));
        if (heartBeat == null) {
            heartBeat = new Timer();
            heartBeat.schedule(new TimerTask() {
                @Override
                public void run() {
                    byte[] b = {
                            0, 0, 0, 0, //封包总大小
                            0, 16, //头部长度
                            0, 1, //协议版本
                            0, 0, 0, 2, //操作码 2为心跳包
                            0, 0, 0, 1 //1
                    };
                    BaseWebSocketClient.this.send(b);
                }
            }, 0, 30000);
        }

    }

    private byte[] getCertification(String jsonString) {
        byte[] bytes = jsonString.getBytes();
        ByteBuffer bb = ByteBuffer.allocate(bytes.length + 16);
        bb.putInt(bytes.length + 16);
        bb.putShort((short) 16);
        bb.putShort((short) 1);
        bb.putInt(7);
        bb.putInt(1);
        bb.put(bytes);
        return bb.array();
    }

    @Override
    public void onMessage(String s) {
        System.out.println("收到消息：" + s);
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            decode(bytes, result -> {
                if ((int) result.get("op") == 5) {
                    List<JSONObject> body = (List<JSONObject>) result.get("body");
                    for (JSONObject jsonObject : body) {
                        if (BiliLiveDanmu.printLog) {
                            System.out.println(jsonObject);
                        }
                        String cmd = jsonObject.getString("cmd");
                        switch (cmd) {
                            case "DANMU_MSG":
                                JSONArray jsonArray = jsonObject.getJSONArray("info");
                                String extraString = jsonArray.getJSONArray(0).getJSONObject(15).getString("extra");
                                JSONObject extra = JSONObject.parseObject(extraString);
                                Color color = new Color();
                                Color.rgb888ToColor(color, extra.getIntValue("color"));
                                color.a = 1.0F;
                                final Color finalColor = color;
                                if (BiliLiveDanmu.showVfx) {
                                    BiliLiveDanmu.addToBot(new AbstractGameAction() {
                                        @Override
                                        public void update() {
                                            AbstractDungeon.topLevelEffects.add(new DanmuEffect(jsonArray.getString(1), MathUtils.random(19), finalColor));
                                            isDone = true;
                                        }
                                    });
                                }
                                if (this.messageCallback != null) {
                                    this.messageCallback.onDanmu(jsonArray.getJSONArray(2).getString(1), jsonArray.getString(1), color);
                                }
                                break;
                            case "WATCHED_CHANGE":
                                JSONObject data = jsonObject.getJSONObject("data");
                                if (this.messageCallback != null) {
                                    this.messageCallback.watchedChange(data.getInteger("num"));
                                }
                                break;
                            case "SEND_GIFT":
                                data = jsonObject.getJSONObject("data");
                                if (this.messageCallback != null) {
                                    this.messageCallback.onGift(data.getString("uname"), data.getString("giftName"), data.getString("action"), data.getInteger("num"));
                                }
                                break;
                            case "INTERACT_WORD":
                                data = jsonObject.getJSONObject("data");
                                if (this.messageCallback != null && BiliLiveDanmu.showEnterRoom) {
                                    this.messageCallback.enterRoom(data.getString("uname"));
                                }
                                break;
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void decode(ByteBuffer bb, Consumer<Map<String, Object>> callback) throws IOException {
        Map<String, Object> result = new HashMap<>();
        bb.mark();
        byte[] bytes = bb.array();
        result.put("packetLen", bb.getInt());
        result.put("headerLen", bb.getShort());
        result.put("ver", bb.getShort());
        result.put("op", bb.getInt());
        result.put("seq", bb.getInt());
        result.put("body", new ArrayList<JSONObject>());
        if ((int) result.get("op") == 5) {
            int offset = 0;
            bb.reset();
            while (offset < bytes.length) {
                bb.mark();
                int packetLen = bb.getInt();
                short headerLen = bb.getShort();
                short ver = bb.getShort();
                int op = bb.getInt();
                int seq = bb.getInt();
                ByteBuffer data = bb.slice();
                byte[] dataArray = data.array();
                String body;
                if (ver == 2) { //有压缩
                    body = new String(ZLibUtils.decompress(new ByteBufferInputStream(data)), StandardCharsets.UTF_8);
                } else { //无压缩
                    body = new String(dataArray, StandardCharsets.UTF_8);
                }
                String[] group = body.split("[\\x00-\\x1f]+");
                for (String s : group) {
                    if (JSONObject.isValidObject(s)) {
                        List<JSONObject> resultList = (List<JSONObject>) result.get("body");
                        resultList.add(JSONObject.parseObject(s));
                    }
                }
                offset += packetLen;
            }
        }
        callback.accept(result);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("连接关闭");
        if (heartBeat != null) {
            heartBeat.cancel();
            heartBeat = null;
            messageCallback = null;
        }
    }

    @Override
    public void onError(Exception e) {
        System.out.println("发生异常");
        if (heartBeat != null) {
            heartBeat.cancel();
            heartBeat = null;
        }
        e.printStackTrace();
    }
}
