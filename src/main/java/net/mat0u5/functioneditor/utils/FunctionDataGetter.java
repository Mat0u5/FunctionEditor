package net.mat0u5.functioneditor.utils;

public class FunctionDataGetter {
    public static String getFunctionDataJson() {
        return tempData();
    }
    private static String tempData() {
        return "[{\"name\":\"my_function\",\"content\":\"/say Hello World\"}]";
    }
}
