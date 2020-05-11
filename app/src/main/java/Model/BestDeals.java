package Model;

public class BestDeals {
    private String menu_id, food_id, name, image;
    private Long price;

    public BestDeals() {
    }

    public BestDeals(String menu_id, String food_id, String name, String image, Long price) {
        this.menu_id = menu_id;
        this.food_id = food_id;
        this.name = name;
        this.image = image;
        this.price = price;
    }

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}
