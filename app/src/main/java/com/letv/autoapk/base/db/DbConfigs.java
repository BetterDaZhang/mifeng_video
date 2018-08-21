package com.letv.autoapk.base.db;

import org.xutils.DbManager;

import com.lecloud.xutils.exception.DbException;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.ui.channel.ChannelVideoInfo;



public interface DbConfigs {
	String DBNAME = "mydata";
	int DBVERSION = 2;
	DbManager.DbUpgradeListener UPDATELISTENER = new DbManager.DbUpgradeListener() {
        @Override
        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
            // TODO: ...
            // db.addColumn(...);
            // db.dropTable(...);
            // ...
            if(oldVersion<2&&newVersion>=2){
                try {
                    db.addColumn(ChannelVideoInfo.class, "mChannelDetailType");
                } catch (Throwable e) {
                    Logger.log(e);
                } 
            }
           
        }
    };
}
