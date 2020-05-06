package Model;

import java.util.List;

public class Food {
    private String name, image, id, description;
    private Long price;
    private List<AddOn> addon;
    private List<SizeModel> size;

    //for cart

    private List<AddOn> userSelectedAddon;
    private SizeModel userSelectedSize;

    public Food() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public List<AddOn> getAddon() {
        return addon;
    }

    public void setAddon(List<AddOn> addon) {
        this.addon = addon;
    }

    public List<SizeModel> getSize() {
        return size;
    }

    public void setSize(List<SizeModel> size) {
        this.size = size;
    }

    public List<AddOn> getUserSelectedAddon() {
        return userSelectedAddon;
    }

    public void setUserSelectedAddon(List<AddOn> userSelectedAddon) {
        this.userSelectedAddon = userSelectedAddon;
    }

    public SizeModel getUserSelectedSize() {
        return userSelectedSize;
    }

    public void setUserSelectedSize(SizeModel userSelectedSize) {
        this.userSelectedSize = userSelectedSize;
    }
}
