package techacademy.wakou.youko.taskapp;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by appu2 on 2017/12/09.
 */

public class TaskApp extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        Realm.init(this);
    }
}
