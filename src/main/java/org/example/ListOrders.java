package org.example;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ListOrders {
    private List<Orders> orders;
    private PageInfo pageInfo;
    private List<AvailableStations> availableStations;

}
