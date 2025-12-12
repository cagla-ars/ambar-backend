# Ambar / Depo Yönetim Sistemi – Backend

Bu proje, stok ve ürün yönetimi ihtiyacını karşılamak için geliştirilmiş,  
JWT tabanlı güvenlik, rol bazlı yetkilendirme ve transaction (hareket) kayıtları içeren  
kurumsal seviyede bir Spring Boot backend uygulamasıdır.

Proje; admin ve kullanıcı rollerine sahip, ürün ekleme–güncelleme–silme işlemlerini kayıt altına alan
bir ambar/depo yönetim sistemi sunar.

---

## Özellikler

- JWT tabanlı kimlik doğrulama ve yetkilendirme
- Rol bazlı erişim kontrolü (ADMIN / USER)
- Ürün yönetimi (CRUD)
- Stok hareketlerinin kayıt altına alınması (Transaction log)
- Soft delete desteği
- Mail ile geçici şifre gönderimi
- DTO ve Bean Validation kullanımı
- Global Exception Handling
- Environment variable (ENV) ile gizli bilgilerin yönetimi

---

## Kullanılan Teknolojiler

- Java 17
- Spring Boot
- Spring Security
- JWT (jjwt)
- Spring Data JPA (Hibernate)
- SQL Server
- Bean Validation (jakarta.validation)
- Maven
- Lombok (kısmi kullanım)

---

## Roller ve Yetkiler

### ADMIN
- Tüm kullanıcıları görüntüleyebilir
- Tüm ürünleri ve transaction kayıtlarını görebilir
- Kullanıcı ekleyebilir ve silebilir

### USER
- Sadece kendisine ait ürünleri görüntüleyebilir
- Ürün ekleme, güncelleme ve silme işlemleri yapabilir
- Kendi işlemlerine ait transaction kayıtları oluşturur

---

## Güvenlik Mimarisi

- Login sonrası JWT token üretilir
- Token içinde kullanıcı adı ve rol bilgisi bulunur
- Korumalı endpoint’ler `@PreAuthorize` ile güvence altına alınmıştır
- JWT secret ve diğer hassas bilgiler kod içinde tutulmaz, environment variable olarak yönetilir

---

## Ürün ve Stok Yönetimi

- Ürün ekleme
- Ürün güncelleme
- Ürün silme (soft delete)
- Stok miktarı değiştiğinde otomatik olarak transaction kaydı oluşturulur  
  (ADD, UPDATE, REMOVE)

---

## Transaction (Hareket) Sistemi

Her stok değişikliği aşağıdaki bilgilerle kayıt altına alınır:

- Ürün
- Kullanıcı
- İşlem tipi
- Miktar
- Tarih

Bu yapı sayesinde tüm stok hareketleri izlenebilir durumdadır.

---

## Validation ve Hata Yönetimi

- Request verileri DTO’lar üzerinden alınır
- `@NotBlank`, `@Email`, `@PositiveOrZero` gibi anotasyonlarla doğrulama yapılır
- Validation ve runtime hataları GlobalExceptionHandler ile tek merkezden yönetilir

Validation hata örneği
{
  "email": "Geçerli bir email giriniz"
}

## Gizli Bilgiler ve Ortam Ayarları

Bu projede veritabanı bilgileri, mail hesapları ve JWT secret gibi hassas veriler
doğrudan kod içinde tutulmaz.
Tüm gizli bilgiler environment variable (ortam değişkenleri) üzerinden yönetilir.

Bu yapı sayesinde proje GitHub üzerinde güvenli kalır ve farklı ortamlarda
kolayca çalıştırılabilir.

Örnek .env / applicationExample:

```env
DB_URL=
DB_USERNAME=
DB_PASSWORD=
MAIL_USERNAME=
MAIL_PASSWORD=
JWT_SECRET=
Gerçek veriler GitHub reposuna eklenmez.

KURULUM VE ÇALIŞTIRMA

Aşağıdaki adımları izleyerek backend uygulamasını lokal ortamda çalıştırabilirsiniz.

Projeyi klonlayın:

git clone https://github.com/kullanici-adi/ambar-backend.git
Environment değişkenlerini ayarlayın.

Uygulamayı çalıştırın:

mvn spring-boot:run

Backend varsayılan olarak aşağıdaki adreste çalışır:

http://localhost:8081
Örnek API Endpoint’leri
Method Endpoint Açıklama
POST /api/auth/login Kullanıcı girişi
POST /api/users Kullanıcı oluştur
GET /api/products Ürünleri getir
POST /api/products Ürün ekle
PUT /api/products/{id} Ürün güncelle
DELETE /api/products/{id} Ürün sil
GET /api/transactions Stok hareketleri

*NOTLAR*

*AŞAĞIDAKİ FRONTENTLE BİRLİKTE ÇALIŞIR*

Frontend repository:
https://github.com/cagla-ars/ambar-frontend

Proje eğitim ve gerçek kullanım senaryoları dikkate alınarak geliştirilmiştir.
Frontend uygulamalarla entegre çalışmaya uygundur.
Temiz mimari ve güvenlik öncelikli bir yapı hedeflenmiştir.

*GELİŞTİRİCİ*

Bu proje Spring Boot backend geliştirme pratiği kazanmak ve
kurumsal mimari yaklaşımını öğrenmek amacıyla geliştirilmiştir.

Geri bildirimlere açıktır.
