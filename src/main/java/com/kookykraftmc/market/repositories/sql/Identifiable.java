package com.kookykraftmc.market.repositories.sql;

public interface Identifiable {
    <ID> ID getId();
    <ID> void setId(ID id);
}
