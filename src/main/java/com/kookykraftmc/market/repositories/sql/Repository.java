package com.kookykraftmc.market.repositories.sql;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class Repository<ID, T> {
    private String dbUri;

    @Inject
    private Logger logger;
    private SqlService sql;

    public void init(String dbUri) {
        this.dbUri = dbUri;
        try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement stmt = createTablePrepareStatement(conn);
            stmt.execute();
        } catch (SQLException e) {
            logger.error("Unable to create SQL table", e);
        }
    }

    public Optional<T> insert(T t) {
        try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement stmt = createInsertPrepareStatement(conn, t);
            stmt.executeUpdate();
            return Optional.ofNullable(t);
        } catch (SQLException e) {
            logger.error("Unable to SQL addListing", e);
            return Optional.empty();
        }
    }

    public boolean delete(ID id) {
        try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement stmt = createDeletePrepareStatement(conn, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Unable to SQL addListing", e);
            return false;
        }
    }

    protected abstract PreparedStatement createDeletePrepareStatement(Connection conn, ID id) throws SQLException;

    protected abstract PreparedStatement createInsertPrepareStatement(Connection conn, T t) throws SQLException;


    protected DataSource getDataSource() throws SQLException {
        if (sql == null) {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return sql.getDataSource(dbUri);
    }

    protected Optional<Map<String, String>> get(ID id) {
        try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement stmt = createGetPrepareStatement(conn, id);
            ResultSet results = stmt.executeQuery();
            return toMap(results).stream().findFirst();
        } catch (SQLException e) {
            logger.error("Unable to SQL select", e);
            return Optional.empty();
        }
    }

    protected List<Map<String, String>> selectAll() {
        try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement stmt = createGetAllPrepareStatement(conn);
            ResultSet results = stmt.executeQuery();
            return toMap(results);
        } catch (SQLException e) {
            logger.error("Unable to SQL select *", e);
        }

        return Lists.newArrayList();
    }

    protected List<Map<String, String>> toMap(ResultSet results) throws SQLException {
        List<Map<String, String>> result = Lists.newArrayList();
        while (results.next()) {
            ResultSetMetaData metaData = results.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < columnCount; i++) {
                map.put(metaData.getColumnLabel(i + 1), results.getString(i + 1));
            }
            result.add(map);
        }
        return result;
    }

    protected abstract PreparedStatement createGetPrepareStatement(Connection conn, ID id) throws SQLException;

    protected abstract PreparedStatement createGetAllPrepareStatement(Connection conn) throws SQLException;

    protected abstract PreparedStatement createTablePrepareStatement(Connection conn) throws SQLException;
}
