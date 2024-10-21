package ssafy.sera.common.util;

import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ParameterUtil {

    public static boolean isEmptyString(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isEmptySet(Set<?> set) {
        return set == null || set.isEmpty();
    }

    public static String blankToNull(String str) {
        return str == null || str.trim().isEmpty() ? null : str;
    }

    public static String nullToBlank(String str) {
        return str == null ? "" : str;
    }

    public static <T> Set<T> nullToEmptySet(Set<T> set) {
        return set == null ? new HashSet<>() : set;
    }

    public static boolean nullToFalse(Boolean bool){
        return bool != null && bool;
    }

    public static Integer nullToZero(Integer num){
        return num == null ? 0 : num;
    }

}
