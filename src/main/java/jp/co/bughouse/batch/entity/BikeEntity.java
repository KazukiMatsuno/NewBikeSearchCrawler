/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch.entity;

/**
 *
 * @author user
 */
public class BikeEntity {

    private String shopUrl;
    private String maker;
    private String name;
    private String color;
    private String comment;
    private String picUrl;
    private String url;

    private Integer distance;
    private Integer price;
    private Integer inspection;
    private Integer year;

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setInspection(Integer inspection) {
        this.inspection = inspection;
    }

    public String getShopUrl() {
        return shopUrl;
    }

    public String getMaker() {
        return maker;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getComment() {
        return comment;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getUrl() {
        return url;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getInspection() {
        return inspection;
    }

    public Integer getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "shopUrl:" + shopUrl + "\t"
                + "maker:" + maker + "\t"
                + "name:" + name + "\t"
                + "color:" + color + "\t"
                + "comment:" + comment + "\t"
                + "picUrl:" + picUrl + "\t"
                + "url:" + url + "\t"
                + "distance:" + distance + "\t"
                + "price:" + price + "\t"
                + "inspection:" + inspection + "\t"
                + "year:" + year;
    }
}
