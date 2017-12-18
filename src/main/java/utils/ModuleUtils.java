package utils;

import org.apache.commons.collections4.Transformer;

import java.util.ArrayList;
import java.util.List;

public class ModuleUtils {

    public static <S,T> List<T> transformArrayToList(S[] array, Transformer<S,T> transformer ) {
            List<T> toReturn = new ArrayList<>();
            for(S value : array) {
                if(value != null) {
                    T fetchValue = transformer.transform(value);
                    toReturn.add(fetchValue);
                }
            }
            return toReturn;
    }

}
