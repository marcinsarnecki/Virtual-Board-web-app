package com.ctdp.springproject.model;

import javax.persistence.*;

@Entity
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Color color;
    @ManyToOne
    @JoinColumn(name = "board_record_id")
    private BoardRecord boardRecord;
    public Badge() {
    }

    public Badge(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Badge{" +
                "id=" + id +
                ", color=" + color +
                '}';
    }
}
