package com.mylist.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
public class WatchItem extends BaseItem {

    @Enumerated(EnumType.STRING)
    private WatchType type;

    @jakarta.persistence.ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
    private java.util.List<String> genres;

    private String category;

    public WatchType getType() {
        return type;
    }

    public void setType(WatchType type) {
        this.type = type;
    }

    public java.util.List<String> getGenres() {
        return genres;
    }

    public void setGenres(java.util.List<String> genres) {
        this.genres = genres;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
