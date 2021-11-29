package ru.skillbox.diplom.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static de.flapdoodle.embed.process.runtime.Network.getFreeServerPort;
import static java.lang.String.format;
import static java.net.InetAddress.getLocalHost;

/**
 * Only for local run and tests
 */
@Configuration
@EnableTransactionManagement
@Slf4j
@Profile("local")
public class DbConfig {
    @Value("${embedded-postgresql.database}")
    private String database;
    @Value("${embedded-postgresql.schema}")
    private String schema;
    @Value("${embedded-postgresql.user}")
    private String user;
    @Value("${embedded-postgresql.password}")
    private String password;

    private static final List<String> DEFAULT_ADDITIONAL_INIT_DB_PARAMS = Arrays
            .asList("--nosync", "--locale=en_US.UTF-8");

    @Bean
    @DependsOn("postgresProcess")
    public DataSource dataSource(PostgresConfig config) throws SQLException {
        log.info("Start embedded postgresql {}:{}/{}",
                config.net().host(), config.net().port(), config.storage().dbName());

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(format("jdbc:postgresql://%s:%s/%s?user=%s&password=%s",
                config.net().host(), config.net().port(), config.storage().dbName(),
                config.credentials().username(), config.credentials().password()));
        ds.setUsername(config.credentials().username());
        ds.setPassword(config.credentials().password());

        final Connection conn = DriverManager.getConnection(ds.getUrl());
        conn.createStatement().execute(String.format("CREATE schema %s", schema));

        return ds;
    }

    @Bean
    public PostgresConfig postgresConfig() throws IOException {

        final PostgresConfig postgresConfig = new PostgresConfig(Version.V9_6_11,
                new AbstractPostgresConfig.Net(getLocalHost().getHostAddress(), getFreeServerPort()),
                new AbstractPostgresConfig.Storage(database),
                new AbstractPostgresConfig.Timeout(),
                new AbstractPostgresConfig.Credentials(user, password)
        );

        postgresConfig.getAdditionalInitDbParams().addAll(DEFAULT_ADDITIONAL_INIT_DB_PARAMS);

        return postgresConfig;
    }

    @Bean(destroyMethod = "stop")
    public PostgresProcess postgresProcess(PostgresConfig config) throws IOException {
        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
        PostgresExecutable exec = runtime.prepare(config);
        return exec.start();
    }
}
