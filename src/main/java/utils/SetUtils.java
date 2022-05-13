package utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetUtils {
    public <T> Set<T> findDuplicates(List<T> listContainingDuplicates) {
        final Set<T> setToReturn = new HashSet<>();
        final Set<T> set1 = new HashSet<>();

        for (T element : listContainingDuplicates) {
            if (!set1.add(element)) {
                setToReturn.add(element);
            }
        }
        return setToReturn;
    }

}
