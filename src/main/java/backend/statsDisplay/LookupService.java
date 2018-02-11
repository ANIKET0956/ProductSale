package backend.statsDisplay;

import backend.ProductDetails;

import java.util.List;

public interface LookupService<Type> {

    List<Type> getPresentProductDetails();

    void saveProduct(Type type);

}
