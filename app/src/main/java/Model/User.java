package Model;

public class User {
    private String name, email, password, phone, uid, count, address;

    public User() {
    }

    public User(String uid, String name, String address, String phone) {
        this.uid = uid;
        this.name = name;
        this.address =address;
        this.phone = phone;
    }

//    public User(String name, String email, String password, String phone) {
//        this.name = name;
//        this.email = email;
//        this.password = password;
//        this.phone = phone;
//    }

    public User(String name, String email, String password, String phone, String uid, String count, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.uid = uid;
        this.count = count;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
