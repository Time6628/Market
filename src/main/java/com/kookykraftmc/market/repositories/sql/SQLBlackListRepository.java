package com.kookykraftmc.market.repositories.sql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.model.BlackListItem;
import com.kookykraftmc.market.model.ItemStackId;
import com.kookykraftmc.market.repositories.BlackListRepository;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

@Singleton
public class SQLBlackListRepository implements BlackListRepository<MarketConfig.SqlDataStoreConfig> {

    @Inject
    private Logger logger;
    private SqlService sql;
    private String dbUri;


    @Override
    public void init(MarketConfig.SqlDataStoreConfig sqlDataStoreConfig) {
        this.dbUri = sqlDataStoreConfig.dbUri;

        try (Connection conn = getDataSource().getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS BLACKLIST(ID VARCHAR(100) PRIMARY KEY; ";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            logger.error("Unable to create SQL table", e);
        }
    }

    private DataSource getDataSource() throws SQLException {
        if (sql == null) {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return sql.getDataSource(dbUri);
    }

    @Override
    public Stream<BlackListItem> all() {
        return null;
    }

    @Override
    public boolean deleteById(ItemStackId id) {
        return false;
    }

    @Override
    public boolean add(BlackListItem blackListItem) {
        return false;
    }

}
