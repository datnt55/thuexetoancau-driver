package grab.com.thuexetoancau.driver.model;

import java.io.Serializable;

/**
 * Created by DatNT on 7/21/2017.
 */

public class User implements Serializable{
    private int id;
    private String name;
    private String phone;
    private String email;
    private String url;
    private String carModel;
    private String carMade;
    private String carYear;
    private int carSize;
    private String carNumber;
    private String carType;
    private long carPrice;
    private long totalMoney;
    private String province;
    private String identity;
    private String license;

    public User(String name, String phone, String email, String carModel, String carMade, String carYear, int carSize, String carNumber, String carType, long carPrice, long totalMoney, String province, String identity, String license) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.carModel = carModel;
        this.carMade = carMade;
        this.carYear = carYear;
        this.carSize = carSize;
        this.carNumber = carNumber;
        this.carType = carType;
        this.carPrice = carPrice;
        this.totalMoney = totalMoney;
        this.province = province;
        this.identity = identity;
        this.license = license;
    }

    public User(int id, String name, String phone, String email, String url) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
