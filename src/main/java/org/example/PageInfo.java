package org.example;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PageInfo {
    private int page;
    private int total;
    private int limit;

}
