package com.kookykraftmc.market.repositories;

import com.kookykraftmc.market.model.BlackListItem;
import com.kookykraftmc.market.model.ItemStackId;

import java.util.stream.Stream;

public interface BlackListRepository<CONFIG> {

    void init(CONFIG config);
    Stream<BlackListItem> all();

    boolean deleteById(ItemStackId id);

    boolean add(BlackListItem blackListItem);
}
