package com.kookykraftmc.market.repositories.sql;

import com.google.inject.Singleton;
import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.model.BlackListItem;
import com.kookykraftmc.market.model.ItemStackId;
import com.kookykraftmc.market.repositories.BlackListRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Stream;

@Singleton
public class SQLBlackListRepository extends Repository<ItemStackId, BlackListItem> implements BlackListRepository<MarketConfig.SqlDataStoreConfig> {

    @Override
    public void init(MarketConfig.SqlDataStoreConfig sqlDataStoreConfig) {
        super.init(sqlDataStoreConfig.dbUri);
    }

    @Override
    public Stream<BlackListItem> all() {
        return super.selectAll().stream().map(this::toBlackListItem);
    }

    private BlackListItem toBlackListItem(Map<String, String> blMap) {
        return new BlackListItem(new ItemStackId(blMap.get("ID")));
    }

    @Override
    public boolean deleteById(ItemStackId id) {
        return super.delete(id);
    }

    @Override
    protected PreparedStatement createDeletePrepareStatement(Connection conn, ItemStackId itemStackId) throws SQLException {
        String sql = "DELETE FROM BLACKLIST WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, itemStackId.get());
        return stmt;
    }

    @Override
    public boolean add(BlackListItem blackListItem) {
        return super.insert(blackListItem).isPresent();
    }

    protected PreparedStatement createGetPrepareStatement(Connection connection, ItemStackId id) throws SQLException {
        String sql = "SELECT * FROM BLACKLIST WHERE ID = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, id.get());
        return stmt;
    }

    @Override
    protected PreparedStatement createTablePrepareStatement(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS BLACKLIST(ID VARCHAR(100) PRIMARY KEY); ";
        PreparedStatement stmt = conn.prepareStatement(sql);
        return stmt;
    }

    protected PreparedStatement createInsertPrepareStatement(Connection connection, BlackListItem blackListItem) throws SQLException {
        String sql = "INSERT INTO BLACKLIST"
                + "(ID) VALUES"
                + "(?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, blackListItem.getId().get());
        return stmt;
    }

    @Override
    protected PreparedStatement createGetAllPrepareStatement(Connection connection) throws SQLException {
        String sql = "SELECT * FROM BLACKLIST";
        PreparedStatement stmt = connection.prepareStatement(sql);
        return stmt;
    }
}
