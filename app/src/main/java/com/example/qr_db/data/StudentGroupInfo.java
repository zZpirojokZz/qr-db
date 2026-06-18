package com.example.qr_db.data;

import com.google.gson.annotations.SerializedName;

/**
 * Класс для хранения информации о группе студента.
 * Используется в StudentViewModel для загрузки данных о группе и последующего получения списка предметов.
 */
public class StudentGroupInfo {

    @SerializedName("group_id")
    public int group_id;

    @SerializedName("group_name")
    public String group_name;

    @SerializedName("starosta_id")
    public Integer starosta_id;

    public StudentGroupInfo() {
    }

    public StudentGroupInfo(int group_id, String group_name, Integer starosta_id) {
        this.group_id = group_id;
        this.group_name = group_name;
        this.starosta_id = starosta_id;
    }

    // Геттеры и сеттеры (для совместимости и Java-логики, 
    // хотя в Kotlin можно обращаться напрямую к полям)
    
    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public Integer getStarosta_id() {
        return starosta_id;
    }

    public void setStarosta_id(Integer starosta_id) {
        this.starosta_id = starosta_id;
    }
}
