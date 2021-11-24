package xxx.xxx.glass.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Currently unused manager for (My)SQL database connections in a pool,
 * the (My)SQL implementation of the plugin is unfinished.
 */

public class ConnectionPoolManager {

    private boolean setup = false;
    private HikariDataSource dataSource;

    public ConnectionPoolManager(final FileConfiguration config) {

        try {
            final HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(
                    String.format("jdbc:mysql://%s:%d/%s",
                            config.getString("host"),
                            config.getInt("port", 3306),
                            config.getString("database"))
            );
            hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
            hikariConfig.setUsername(config.getString("username"));
            hikariConfig.setPassword(config.getString("password"));
            hikariConfig.setConnectionTimeout(config.getLong("connection-timeout", 30000L));
            hikariConfig.setIdleTimeout(config.getLong("idle-timeout", 600000L));
            hikariConfig.setMaxLifetime(config.getLong("max-lifetime", 1800000L));
            hikariConfig.setMinimumIdle(config.getInt("minimum-idle", 10));
            hikariConfig.setMaximumPoolSize(config.getInt("maximum-pool-size", 10));

            dataSource = new HikariDataSource(hikariConfig);
            setup = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public Connection getConnection() throws SQLException {

        return dataSource.getConnection();

    }

    public void close(final Connection connection, final PreparedStatement statement, final ResultSet resultSet) {

        if (connection != null) try {
            connection.close();
        } catch (SQLException ignored) {
        }

        if (statement != null) try {
            statement.close();
        } catch (SQLException ignored) {
        }

        if (resultSet != null) try {
            resultSet.close();
        } catch (SQLException ignored) {
        }

    }

    public void closePool() {

        if (dataSource != null && !dataSource.isClosed())
            dataSource.close();

    }

    public boolean isSetup() {

        return setup;

    }

}
