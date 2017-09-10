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
    private int driverType;
    private int isCar;

    public User(String name, String phone, String email, String carModel, String carMade, String carYear, int carSize, String carNumber, String carType, long carPrice, long totalMoney, String province, String identity, String license, int driverType, int isCar) {
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
        this.driverType = driverType;
        this.isCar = isCar;
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

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarMade() {
        return carMade;
    }

    public void setCarMade(String carMade) {
        this.carMade = carMade;
    }

    public String getCarYear() {
        return carYear;
    }

    public void setCarYear(String carYear) {
        this.carYear = carYear;
    }

    public int getCarSize() {
        return carSize;
    }

    public void setCarSize(int carSize) {
        this.carSize = carSize;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public long getCarPrice() {
        return carPrice;
    }

    public void setCarPrice(long carPrice) {
        this.carPrice = carPrice;
    }

    public long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public int getDriverType() {
        return driverType;
    }

    public void setDriverType(int driverType) {
        this.driverType = driverType;
    }

    public int getIsCar() {
        return isCar;
    }

    public void setIsCar(int isCar) {
        this.isCar = isCar;
    }
}
