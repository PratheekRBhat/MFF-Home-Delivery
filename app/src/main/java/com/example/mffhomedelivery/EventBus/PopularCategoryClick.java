package com.example.mffhomedelivery.EventBus;

import Model.PopularCategories;

public class PopularCategoryClick {
    private PopularCategories popularCategories;

    public PopularCategoryClick(PopularCategories popularCategories) {
        this.popularCategories = popularCategories;
    }

    public PopularCategories getPopularCategories() {
        return popularCategories;
    }

    public void setPopularCategories(PopularCategories popularCategories) {
        this.popularCategories = popularCategories;
    }
}
