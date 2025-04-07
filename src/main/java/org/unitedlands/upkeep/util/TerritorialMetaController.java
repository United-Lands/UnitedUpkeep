package org.unitedlands.upkeep.util;

import com.palmergames.bukkit.towny.object.TownyObject;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.LongDataField;
import com.palmergames.bukkit.towny.utils.MetaDataUtil;

public class TerritorialMetaController {
    private static final BooleanDataField TERRITORIAL_WAR_KEY = new BooleanDataField("territorial_war ", true);
    private static final LongDataField TERRITORIAL_WAR_COOLDOWN_KEY = new LongDataField("territorial_war_last_switch ", 0L);

    public TerritorialMetaController() {
    }

    public static void toggleTerritorialWars(TownyObject obj) {
        BooleanDataField bdf = (BooleanDataField)TERRITORIAL_WAR_KEY.clone();
        if (obj.hasMeta(bdf.getKey())) {
            MetaDataUtil.setBoolean(obj, bdf, !MetaDataUtil.getBoolean(obj, bdf), true);
        } else {
            MetaDataUtil.addNewBooleanMeta(obj, bdf.getKey(), true, true);
        }

    }

    public static boolean toggledTerritorialWars(TownyObject obj) {
        BooleanDataField bdf = (BooleanDataField)TERRITORIAL_WAR_KEY.clone();
        return MetaDataUtil.getBoolean(obj, bdf);
    }

    public static void setTerritorialWarSwitchTime(TownyObject obj) {
        LongDataField ldf = (LongDataField)TERRITORIAL_WAR_COOLDOWN_KEY.clone();
        if (obj.hasMeta(ldf.getKey())) {
            MetaDataUtil.setLong(obj, ldf, System.currentTimeMillis(), true);
        } else {
            MetaDataUtil.addNewLongMeta(obj, ldf.getKey(), System.currentTimeMillis(), true);
        }
    }

    public static Long getTerritorialWarSwitchTime(TownyObject obj) {
        LongDataField ldf = (LongDataField)TERRITORIAL_WAR_COOLDOWN_KEY.clone();
        return MetaDataUtil.getLong(obj, ldf);
    }

}
