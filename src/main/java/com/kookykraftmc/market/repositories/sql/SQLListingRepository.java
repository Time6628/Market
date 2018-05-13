package com.kookykraftmc.market.repositories.sql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.model.Listing;
import com.kookykraftmc.market.repositories.ListingRepository;
import com.kookykraftmc.market.service.ItemSerializer;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Singleton
public class SQLListingRepository extends Repository<String, Listing> implements ListingRepository<MarketConfig.SQLDataStoreConfig> {

    @Inject
    private Logger logger;
    @Inject
    private ItemSerializer itemSerializer;

    private SqlService sql;
    private String dbUri;


    @Override
    public void init(MarketConfig.SQLDataStoreConfig sqlDataStoreConfig) {
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
    public Optional<Listing> addListing(Listing listing) {
        return super.insert(listing);
    }

    @Override
    public Stream<Listing> all() {
        return selectAll().stream().map(this::toListing);
    }

    @Override
    public Stream<Listing> findAllBySellerId(UUID sellerId) {
        try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement stmt = createSelectBySellerPrepareStatement(conn, sellerId);
            ResultSet results = stmt.executeQuery();
            return super.toMap(results).stream().map(this::toListing);
        } catch (SQLException e) {
            logger.error("Unable to SQL select", e);
            return Stream.empty();
        }
    }

    @Override
    public Optional<Listing> getById(String id) {
        return super.get(id).map(this::toListing);
    }

    private Listing toListing(Map<String, String> listingMap) {
        return new Listing(
                listingMap.get("ID"),
                itemSerializer.deserializeItemStack(listingMap.get("ITEM")).get(),
                UUID.fromString(listingMap.get("SELLER")),
                Integer.parseInt(listingMap.get("STOCK")),
                Integer.parseInt(listingMap.get("PRICE")),
                Integer.parseInt(listingMap.get("QTY"))
        );
    }

    @Override
    protected PreparedStatement createGetPrepareStatement(Connection conn, String listingId) throws SQLException {
        String sql = "SELECT * FROM LISTINGS WHERE ID = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, listingId);
        return stmt;
    }

    protected PreparedStatement createSelectBySellerPrepareStatement(Connection conn, UUID sellerId) throws SQLException {
        String sql = "SELECT * FROM LISTINGS WHERE SELLER = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, sellerId.toString());

        return stmt;
    }

    @Override
    protected PreparedStatement createGetAllPrepareStatement(Connection conn) throws SQLException {
        String sql = "SELECT *  FROM LISTINGS";
        PreparedStatement stmt = conn.prepareStatement(sql);
        return stmt;
    }

    @Override
    public void deleteById(String listingId) {
        super.delete(listingId);
    }

    @Override
    protected PreparedStatement createDeletePrepareStatement(Connection conn, String id) throws SQLException {
        String sql = "DELETE FROM LISTINGS WHERE ID = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, id);
        return stmt;
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

    protected PreparedStatement createTablePrepareStatement(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS LISTINGS(ID bigint auto_increment PRIMARY KEY, " +
                "SELLER VARCHAR(100) ," +
                "STOCK INT ," +
                "PRICE INT ," +
                "QTY INT ," +
                "ITEM CLOB);" +
                "CREATE INDEX IF NOT EXISTS SELLER_IND ON LISTINGS (SELLER);";
        return connection.prepareStatement(sql);
    }

}
