package demoMod.bililivedanmu;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModMinMaxSlider;
import basemod.ModPanel;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import demoMod.bililivedanmu.relics.Danmu;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@SpireInitializer
public class BiliLiveDanmu implements EditRelicsSubscriber,
        EditStringsSubscriber,
        PostInitializeSubscriber,
        PostDungeonInitializeSubscriber,
        PostUpdateSubscriber {
    public static boolean showVfx = true;
    public static int maxDanmuSize = 10;
    public static boolean showEnterRoom = false;
    public static boolean printLog = false;
    private static List<AbstractGameAction> actionList = new Vector<>();
    private static List<AbstractGameAction> parallelActions = new Vector<>();

    public static void initialize() throws URISyntaxException {
        new BiliLiveDanmu();
    }

    public BiliLiveDanmu() {
        BaseMod.subscribe(this);
    }

    public static String makeID(String name) {
        return "BiliLiveDanmu:" + name;
    }

    public static String getResourcePath(String path) {
        return "BiliLiveDanmuImages/" + path;
    }

    public static String getLanguageString() {
        String language;
        switch (Settings.language) {
            case ZHS:
                language = "zhs";
                break;
            default:
                language = "eng";
        }
        return language;
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new Danmu(), RelicType.SHARED);
    }

    @Override
    public void receiveEditStrings() {
        String language;
        language = getLanguageString();

        String relicStrings = Gdx.files.internal("localization/" + language + "/Danmu-RelicStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);
        String uiStrings = Gdx.files.internal("localization/" + language + "/Danmu-UIStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(UIStrings.class, uiStrings);
    }

    @Override
    public void receivePostInitialize() {
        loadSettings();
        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ModPanel"));
        ModPanel settingsPanel = new ModPanel();
        ModLabeledToggleButton button = new ModLabeledToggleButton(uiStrings.TEXT[0], 350.0F, 700.0F, Color.WHITE, FontHelper.buttonLabelFont, showVfx, settingsPanel, (me) -> {},
                (me) -> {
                    showVfx = me.enabled;
                    BiliLiveDanmu.saveSettings();
                });
        ModMinMaxSlider slider = new ModMinMaxSlider(uiStrings.TEXT[1], 550.0F, 650.0F, 10, 20, maxDanmuSize, "%.0f", settingsPanel, me -> {
            maxDanmuSize = (int) me.getValue();
            if (Math.abs(me.getValue() - (int) me.getValue()) < 0.05F) {
                BiliLiveDanmu.saveSettings();
            }
        });
        ModLabeledToggleButton button1 = new ModLabeledToggleButton(uiStrings.TEXT[2], 350.0F, 600.0F, Color.WHITE, FontHelper.buttonLabelFont, showEnterRoom, settingsPanel, (me) -> {},
                (me) -> {
                    showEnterRoom = me.enabled;
                    BiliLiveDanmu.saveSettings();
                });
        ModLabeledToggleButton button2 = new ModLabeledToggleButton(uiStrings.TEXT[3], 350.0F, 550.0F, Color.WHITE, FontHelper.buttonLabelFont, printLog, settingsPanel, me -> {},
                me -> {
                    printLog = me.enabled;
                    BiliLiveDanmu.saveSettings();
                });
        settingsPanel.addUIElement(button);
        settingsPanel.addUIElement(slider);
        settingsPanel.addUIElement(button1);
        settingsPanel.addUIElement(button2);
        BaseMod.registerModBadge(new Texture(getResourcePath("ui/badge.png")), "Bili Live Danmu", "Everyone", "TODO", settingsPanel);
    }

    @Override
    public void receivePostDungeonInitialize() {
        if (!AbstractDungeon.player.hasRelic(Danmu.ID)) {
            new Danmu().instantObtain();
        }
    }

    private static void saveSettings() {
        try {
            SpireConfig config = new SpireConfig("danmu", "settings");
            config.setBool("showVfx", showVfx);
            config.setInt("maxDanmuSize", maxDanmuSize);
            config.setBool("showEnterRoom", showEnterRoom);
            config.setBool("printLog", printLog);
            config.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadSettings() {
        try {
            SpireConfig config = new SpireConfig("danmu", "settings");
            config.load();
            if (config.has("showVfx")) {
                showVfx = config.getBool("showVfx");
            }
            if (config.has("maxDanmuSize")) {
                maxDanmuSize = config.getInt("maxDanmuSize");
            }
            if (config.has("showEnterRoom")) {
                showEnterRoom = config.getBool("showEnterRoom");
            }
            if (config.has("printLog")) {
                printLog = config.getBool("printLog");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receivePostUpdate() {
        if (!actionList.isEmpty()) {
            actionList.get(0).update();
            if (actionList.get(0).isDone) {
                actionList.remove(0);
            }
        }
        List<AbstractGameAction> toRemove = new ArrayList<>();
        synchronized (parallelActions) {
            for (AbstractGameAction action : parallelActions) {
                action.update();
                if (action.isDone) {
                    toRemove.add(action);
                }
            }
            parallelActions.removeAll(toRemove);
        }
    }

    public static void addToBot(AbstractGameAction action) {
        actionList.add(action);
    }

    public static void addParallelAction(AbstractGameAction action) {
        parallelActions.add(action);
    }
}
