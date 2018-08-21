package com.letv.autoapk.base.db;

import java.io.IOException;

import org.xutils.DbManager;
import org.xutils.x;

import com.letv.autoapk.common.utils.Logger;

public class DbWrapper implements DbConfigs{
	private DbManager db;
	public DbWrapper(){
		db = openDb();
	}
	public DbManager getDb(){
		return db;
	}
	public DbManager openDb() {
		DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
        .setDbName(DBNAME)
        .setDbVersion(DBVERSION)
        .setDbUpgradeListener(UPDATELISTENER);
		return x.getDb(daoConfig);
	}
	public void closeDb(){
		if(db!=null){
			try {
				db.close();
			} catch (IOException e) {
				Logger.log(e);
			}
		}
	}
}
