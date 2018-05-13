package com.kookykraftmc.market.repositories.sql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.model.ItemStackId;
import com.kookykraftmc.market.model.Listing;
import com.kookykraftmc.market.repositories.ListingRepository;
import com.kookykraftmc.market.service.ItemSerializer;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Singleton
public class SQLListingRepository implements ListingRepository<MarketConfig.SqlDataStoreConfig> {

    @Inject
    private Logger logger;
    @Inject
    private ItemSerializer itemSerializer;

    private SqlService sql;
    private String dbUri;


    @Override
    public void init(MarketConfig.SqlDataStoreConfig sqlDataStoreConfig) {
        this.dbUri = sqlDataStoreConfig.dbUri;

        try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement stmt = createTablePrepareStatement(conn);
            stmt.execute();
        } catch (SQLException e) {
            logger.error("Unable to create SQL table", e);
        }
    }

    protected DataSource getDataSource() throws SQLException {
        if (sql == null) {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return sql.getDataSource(dbUri);
    }

    @Override
    public Listing insert(Listing listing) {
        return null;
    }

    @Override
    public Stream<Listing> all() {
        return null;
    }

    @Override
    public boolean exists(ItemStackId itemStackId, UUID seller) {
        return false;
    }

    @Override
    public Stream<Listing> findAllBySellerId(UUID sellerId) {
        return null;
    }

    @Override
    public Stream<Listing> findAllByItemType(ItemType itemType) {
        return null;
    }

    @Override
    public Optional<Listing> get(String id) {
        return Optional.empty();
    }

    @Override
    public void deleteById(String listingId) {

    }


    protected PreparedStatement createInsertPrepareStatement(Connection connection, Listing listing) throws SQLException {
        String sql = "INSERT INTO LISTINGS"
                + "(SELLER, ITEM, STOCK, PRICE, QTY) VALUES"
                + "(?,?,?,?,?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, listing.getSeller().toString());
        stmt.setString(2, itemSerializer.serializeItem(listing.getItemStack()));
        stmt.setInt(3, listing.getStock());
        stmt.setInt(4, listing.getPrice());
        stmt.setInt(5, listing.getQuantityPerSale());
        return stmt;
    }

    protected PreparedStatement createGetPrepareStatement(Connection conn, Integer id) throws SQLException {
        return null;
    }

    protected PreparedStatement createTablePrepareStatement(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS LISTINGS(ID bigint auto_increment PRIMARY KEY, " +
                "SELLER VARCHAR(100) ," +
                "STOCK INT ," +
                "PRICE INT ," +
                "QTY INT ," +
                "ITEM CLOB);";
        return connection.prepareStatement(sql);
    }

}
