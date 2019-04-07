package com.datas;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity(indexes = {
        @Index(value = "epgId, name", unique = true)
})
public class TVObj {

    @Id(autoincrement = true)
    private long id;
    private int epgId;
    private String name;
@Generated(hash = 80996505)
public TVObj(long id, int epgId, String name) {
    this.id = id;
    this.epgId = epgId;
    this.name = name;
}
@Generated(hash = 415965000)
public TVObj() {
}
public long getId() {
    return this.id;
}
public void setId(long id) {
    this.id = id;
}
public int getEpgId() {
    return this.epgId;
}
public void setEpgId(int epgId) {
    this.epgId = epgId;
}
public String getName() {
    return this.name;
}
public void setName(String name) {
    this.name = name;
}
}
