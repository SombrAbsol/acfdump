package FileUnpackers.Generic;

import java.util.HashSet;
import java.util.Set;

public final class InvertibleExtensions {
    private static final Set<String> invertibleExtensions = new HashSet<>();

    static {
        invertibleExtensions.add("RGCN");
        invertibleExtensions.add("RLCN");
        invertibleExtensions.add("RECN");
        invertibleExtensions.add("RNAN");
        invertibleExtensions.add("RCSN");
    }

    public static boolean isKnown(String ext) {
        return invertibleExtensions.contains(ext);
    }
}
