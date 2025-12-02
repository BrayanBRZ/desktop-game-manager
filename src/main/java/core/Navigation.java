package core;

import utils.MyLinkedList;

public final class Navigation {

    private static final MyLinkedList<String> path = new MyLinkedList<>();

    public static void push(String name) {
        path.add(name);
    }

    public static void pop() {
        if (!path.isEmpty()) {
            path.remove(path.size() - 1);
        }
    }

    public static String getPath() {
        return String.join(" -> ", path);
    }

    public static void clear() {
        path.clear();
    }
}
