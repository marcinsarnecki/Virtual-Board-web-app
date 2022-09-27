package com.ctdp.springproject.dto;

import com.ctdp.springproject.model.Badge;
import com.ctdp.springproject.model.BoardRecord;
import org.hibernate.Hibernate;

public class TableRecordDto {
    public String name;
    public String[] badges = new String[10];


    public TableRecordDto(BoardRecord boardRecord) {
        this.name = boardRecord.getPerson().getName() + " " + boardRecord.getPerson().getSurname();
        int idx = 0;
        for(int i = 0; i < 10; i++)
            badges[i] = "transparent.png";
        Hibernate.initialize(boardRecord.getBadgeList());
        for(Badge badge: boardRecord.getBadgeList()) {
            badges[idx] = badge.getColor().getUrl();
            idx++;
            if(idx >= 10)
                break;
        }
    }
}
