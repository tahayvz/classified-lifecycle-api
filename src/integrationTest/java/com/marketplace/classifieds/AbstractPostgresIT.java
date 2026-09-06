package com.marketplace.classifieds;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Entegrasyon testleri için taban sınıf: gerçek PostgreSQL.
 *
 * <p><b>Konteyner neden elle başlatılıyor?</b> Önce {@code @Testcontainers} +
 * {@code @Container} kullanılıyordu. Tek entegrasyon sınıfı varken sorun çıkarmadı.
 * İkinci sınıf eklenince testler <em>"Connection refused"</em> vermeye başladı.
 *
 * <p>Sebep iki yaşam döngüsünün çakışması: {@code @Testcontainers} konteyneri her
 * test SINIFINDAN sonra durdurur, Spring uygulama bağlamı ise sınıflar arasında
 * ÖNBELLEKTE tutulur ve bağlantı bilgisi yalnızca bağlam kurulurken bir kez okunur.
 * İkinci sınıf çalışırken bağlam hâlâ birinci konteynerin portunu gösteriyordu; o
 * konteyner çoktan ölmüştü.
 *
 * <p>Konteyner burada bir kez başlatılıp hiç durdurulmuyor: JVM ile aynı ömrü
 * yaşıyor, port sabit kalıyor. Temizliği Testcontainers'ın Ryuk konteyneri yapar.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
public abstract class AbstractPostgresIT {

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        POSTGRES.start();
    }
}
