package ru.protei.scriptServer.model.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ResultToSelect {
    private Items[] items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Items {
        private String result;

        private int id;

    }
}
