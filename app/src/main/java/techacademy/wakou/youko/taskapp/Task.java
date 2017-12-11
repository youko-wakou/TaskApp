package techacademy.wakou.youko.taskapp;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by appu2 on 2017/12/09.
 */

public class Task extends RealmObject implements Serializable{
    private String title;
    private String contents;
    private Date date;

    @PrimaryKey
    private int id;

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }
    public String getContents(){
        return contents;
    }
    public void setContents(String contents){
        this.contents = contents;
    }
    public Date getDate(){
        return date;
    }
    public void setDate(Date date){
        this.date = date;
    }
    public int getId(){
        return id;

    }
    public void setId(int id){
        this.id = id;
    }
}
