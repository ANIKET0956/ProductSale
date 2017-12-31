package backend;

import backend.enums.IgnoreField;

public class ProductDetails {

    private String id;
    private String productName;
    private String productType;

    private Long purchasedDate;


    private Double marketCost;
    private Double quantityPurchased;
    private Double quantitySold;

    private String storagePlace;

    @IgnoreField(value="selected")
    private Boolean selected;

    public ProductDetails() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Long getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(Long purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public Double getMarketCost() {
        return marketCost;
    }

    public void setMarketCost(Double marketCost) {
        this.marketCost = marketCost;
    }

    public Double getQuantityPurchased() {
        return quantityPurchased;
    }

    public void setQuantityPurchased(Double quantityPurchased) {
        this.quantityPurchased = quantityPurchased;
    }

    public Double getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Double quantitySold) {
        this.quantitySold = quantitySold;
    }

    public String getStoragePlace() {
        return storagePlace;
    }

    public void setStoragePlace(String storagePlace) {
        this.storagePlace = storagePlace;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

}
