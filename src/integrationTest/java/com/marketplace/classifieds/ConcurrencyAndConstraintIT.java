package com.marketplace.classifieds;

import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.port.out.ClassifiedPort;
import com.marketplace.classifieds.domain.port.out.ClassifiedStatusHistoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Eşzamanlılık altında ne oluyor?
 *
 * <p>Bu testler bir review'dan sonra yazıldı. İki savunma eksikti:
 *
 * <ul>
 *   <li>Yinelenen ilan kontrolü "önce sor, sonra yaz" biçimindeydi. İki eşzamanlı
 *       istek arasındaki boşlukta ikisi de kontrolden geçiyor ve ikisi de
 *       yazılıyordu. Veritabanında benzersizlik kısıtı yoktu; mevcut indeks
 *       yalnızca bir indeksti.</li>
 *   <li>{@code @Version} yoktu. İki eşzamanlı durum değişikliği birbirini
 *       sessizce eziyordu: ikisi de aynı önceki durumu okuyor, geçmiş tablosuna
 *       İKİ satır giriyor, ilan tek duruma geçiyordu.</li>
 * </ul>
 *
 * <p>Gerçek PostgreSQL kullanılıyor: benzersizlik kısıtı ve iyimser kilit
 * veritabanı davranışıdır, bellekte taklit edilirse test hiçbir şey kanıtlamaz.
 */
class ConcurrencyAndConstraintIT extends AbstractPostgresIT {

    @Autowired
    private ClassifiedPort classifiedPort;

    @Autowired
    private ClassifiedStatusHistoryPort historyPort;

    private static Classified newClassified(String title) {
        return Classified.builder()
                .title(title)
                .description("Ayni aciklama")
                .category(ClassifiedCategory.EMLAK)
                .createdBy("test")
                .build();
    }

    @Test
    @DisplayName("Ayni ilan iki kez yazilamaz: kisit VERITABANINDA")
    void databaseRejectsDuplicate() {
        // Servisteki "once sor sonra yaz" kontrolu bilerek atlaniyor: test edilen
        // sey uygulamanin kontrolu degil, o kontrol yarisi kaybettiginde
        // veritabaninin ikinci savunma hatti olarak devreye girip girmedigi.
        classifiedPort.save(newClassified("Kisit testi ilani"));

        assertThatThrownBy(() -> classifiedPort.save(newClassified("Kisit testi ilani")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Baslik farkliysa ayni aciklama sorun degil")
    void constraintOnlyCoversTheWholeTriple() {
        // Kisit uc alanin BIRLIKTE tekrarini engelliyor. Yalnizca aciklamanin
        // ayni olmasi mesru bir durum ve engellenmemeli.
        classifiedPort.save(newClassified("Birinci ilan"));
        classifiedPort.save(newClassified("Ikinci ilan"));
    }

    @Test
    @DisplayName("Es zamanli iki durum degisikliginden IKINCISI reddedilir")
    void concurrentStatusChangeIsRejected() {
        Classified saved = classifiedPort.save(newClassified("Kilit testi ilani"));
        Long id = saved.getId();

        // Ayni kaydi iki kez okuyoruz: iki es zamanli istegin gorecegi sey budur.
        Classified first = classifiedPort.findById(id).orElseThrow();
        Classified second = classifiedPort.findById(id).orElseThrow();

        first.setStatus(ClassifiedStatus.AKTIF);
        classifiedPort.save(first);

        second.setStatus(ClassifiedStatus.DEAKTIF);

        // @Version olmadan bu da basariyla yazilir ve birincinin degisikligini
        // sessizce ezerdi.
        assertThatThrownBy(() -> classifiedPort.save(second))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    @DisplayName("Surum alani her yazmada artar")
    void versionIncrementsOnWrite() {
        Classified saved = classifiedPort.save(newClassified("Surum testi ilani"));
        Long firstVersion = saved.getVersion();
        assertThat(firstVersion).isNotNull();

        saved.setStatus(ClassifiedStatus.AKTIF);
        Classified updated = classifiedPort.save(saved);

        assertThat(updated.getVersion()).isGreaterThan(firstVersion);
    }
}
