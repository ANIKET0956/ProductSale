package backend.utility;

import backend.ProductDetails;
import backend.enums.DataType;
import backend.enums.IgnoreField;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.recycler.Recycler;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String beautifyName(String name) {
        String [] words = name.split("(?=[A-Z])");
        if (words.length == 0) {
            return name;
        }
        StringBuilder stringBuilder = new StringBuilder(StringUtils.EMPTY);
        for (int i = 0; i < words.length - 1; i++) {
            stringBuilder.append(firstLetterUpperCase(words[i]));
            stringBuilder.append(" ");
        }
        stringBuilder.append(firstLetterUpperCase(words[words.length-1]));
        return stringBuilder.toString();
    }

    public static String firstLetterUpperCase(String str) {
        if (str == null || str.length() == 0) {
            return  str;
        }
        if (!Character.isUpperCase(str.charAt(0))) {
            return str.substring(0,1).toUpperCase() + str.substring(1);
        }
        return str;
    }

    public static <T> T parseFromString(String json, Class<T> classType) throws IOException {
        return objectMapper.readValue(json,classType);
    }

    public static String convertToString(Object object) throws IOException{
        return objectMapper.writeValueAsString(object);
    }

    public static List<String> getFieldsFromClass(Class tClass) {
        return Arrays.stream(tClass.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
    }

    public static List<String> getFieldNameWithIgnoreCase(Class tClass) {
        List<String> toReturn = new ArrayList<>();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field :fields) {
            Annotation[] annotations = field.getAnnotations();
            boolean toIgnore = Arrays.stream(annotations).anyMatch(annotation -> annotation.annotationType().isAssignableFrom(IgnoreField.class));
            if(!toIgnore){
                toReturn.add(field.getName());
            }
        }
        return toReturn;
    }

    public static List<Tuple<String, DataType>> getFieldsFromClassWithType(Class tClass) {
        List<Tuple<String, DataType>> toReturn = new ArrayList<>();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(String.class)) {
                toReturn.add(new Tuple<>(field.getName(), DataType.STRING));
            } else if (field.getType().isAssignableFrom(Long.class) || field.getType().isAssignableFrom(Integer.class)
                    || field.getType().isAssignableFrom(Double.class) || field.getType().isAssignableFrom(Float.class)) {
                toReturn.add(new Tuple<>(field.getName(), DataType.NUMBER));
            }
        }
        return toReturn;
    }

}
