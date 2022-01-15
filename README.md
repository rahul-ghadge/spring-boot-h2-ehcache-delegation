# spring-boot-h2-ehcache-delegation
Demo project for Spring Boot, Ehcache, h2 db, delegation with BeanPostProcessor.<br/>
In this application delegation is used along with spring boot by overriding BeanPostProcessor interfaces postProcessAfterInitialization method. 

`@EnableJpaRepositories` annotation is used on main class to Enable H2 DB related configuration, which will read properties from `application.properties` file to perform read operations.

`EhcacheDelegationConfig` class is used as delegation, which wraps `SuperHeroRepository` inside `EhcacheSuperHeroRepositoryImpl` if **EhCache** bean is configured in classpath.

Cache will be checked if and only if `Ehcache` bean is configured else `EhcacheSuperHeroRepositoryImpl` will be skipped.


## Prerequisites 
- Java
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/guides/index.html)
- [H2 Database](https://www.h2database.com/html/main.html)
- [Lombok](https://objectcomputing.com/resources/publications/sett/january-2010-reducing-boilerplate-code-with-project-lombok)
- [Ehcache](https://www.ehcache.org/documentation)

## Tools
- IntelliJ IDEA or Eclipse/STS (or any preferred IDE) with embedded Maven
- Maven (version >= 3.6.0)
- Postman (or any RESTful API testing tool), even any web browser would work as we are going to test readonly REST APIs.


<br/>


###  Build and Run application
_GOTO >_ **~/absolute-path-to-directory/spring-boot-h2-ehcache-delegation**  
and try below command in terminal
> **```mvn spring-boot:run```** it will run application as spring boot application

or
> **```mvn clean install```** it will build application and create **jar** file under target directory 

Run jar file from below path with given command
> **```java -jar ~/path-to-spring-boot-h2-ehcache-delegation/target/spring-boot-h2-ehcache-delegation-0.0.1-SNAPSHOT.jar```**

Or
> run main method from `SpringBootH2EhcacheDelegationApplication.java` as spring boot application. 



### Code Snippets
1. #### Maven Dependencies
    Need to add below `JPA` & `H2` dependencies to enable H2 DB related config in **pom.xml**. <br>
    `Lombok` dependency is to get rid of boiler-plate code.<br>
    `AOP` for aspect related features to print logs using `@LogObjectBefore` & `@LogObjectAfter` custom annotations.
    ```
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
   
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
   
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
   
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>   

    <dependency>
        <groupId>net.sf.ehcache</groupId>
        <artifactId>ehcache</artifactId>
    </dependency>
    ```
    
   
   
2. #### Properties file
    Reading H2 DB related properties from **application.properties** file and configuring JPA connection factory for H2 database.  

    **src/main/resources/application.properties**
     ```
     spring.datasource.url=jdbc:h2:mem:sampledb
     spring.datasource.driverClassName=org.h2.Driver
     spring.datasource.username=sa
     spring.datasource.password=password
     spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
    
     spring.h2.console.enabled=true 
     
     # This will enable circular dependencies which we are using for delegation
     spring.main.allow-circular-references=true
     ```
   
   
3. #### Model class
    Below model classes which we will store in H2 DB and perform read operations.  
    **SuperHero.java**  
    ```
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Entity
    @Table
    public class SuperHero implements Serializable {
    
        @Id
        @GeneratedValue
        private int id;
    
        private String name;
        private String superName;
        private String profession;
        private int age;
        private boolean canFly;
    
        // Constructor, Getter and Setter
    }
    ```
 
    #    
    #### The annotations are added in this controller to print log smartly with the help of Aspect (LoggerAspect class from aop package).
    **@LogObjectBefore** - annotation created to print log before method execution <br/>
    **@LogObjectAfter** - annotation created to print the returned value from method
    #   

4. #### Read operation for Super Heroes

    In **SuperHeroController.java** class, 
    we have exposed 5 endpoints for basic CRUD operations
    - GET All Super Heroes
    - GET by ID
    - GET by ID IN


    ```
    @RequiredArgsConstructor
    @RestController
    @RequestMapping("/super-hero")
    public class SuperHeroController {
   
        private final SuperHeroService superHeroService;
        
        @LogObjectAfter
        @GetMapping
        public ResponseEntity<List<?>> findAll();
    
        @LogObjectBefore
        @LogObjectAfter
        @GetMapping("/{id}")
        public ResponseEntity<?> findById(@PathVariable Integer id);
    
        @LogObjectBefore
        @LogObjectAfter
        @GetMapping("/in")
        public ResponseEntity<List<?>> findByIdIn(@RequestParam List<Integer> ids);
    }
    ```
   
   In **SuperHeroServiceImpl.java**, we are injecting SuperHeroRepository interface using constructor injection for read operation.
    
   In **SuperHeroRepository.java**, we are extending `JpaRepository<Class, ID>` interface which has read related methods by putting query inside `@Query` annotation, 
   which we are overriding in **EhcacheSuperHeroRepositoryImpl** class.
    
   ```
   public interface SuperHeroRepository extends JpaRepository<SuperHero, Integer> {
    
        @Query("SELECT s FROM SuperHero s WHERE s.id = ?1")
        Optional<SuperHero> findById(Integer id);

        @Query("SELECT s FROM SuperHero s")
        List<SuperHero> findAll();

        @Query("SELECT s FROM SuperHero s where s.id in (:ids)")
        List<SuperHero> findByIdIn(List<Integer> ids);
   }
   ```
   
5. #### Ehcache Delegation Config class
    This is the most important class in spring **delegation pattern** and in this application also. This will override the default behaviour of spring JPA by returning `EhcacheSuperHeroReoisitoryImpl` class implementation
    by overriding `BeanPostProcessor`'s `postProcessAfterInitialization()` method instead of JPA repository if and only if `Ehcache bean is present in classpath` else default JPA repository will be in a picture. <br/> 
    _In simple words if `Ehcache` bean is configured in application then first SuperHero data will be found in cache and then in DB and will be added in cache after fetching from DB._<br/>
    @ConditionalOnBean(Ehcache.class) will check Ehcache bean is present in application only then this call will get configured.<br/>
    @ConditionalOnClass(Ehcache.class) will check Ehcache class is present in application only then this call will get configured. This will prevent from getting `net.sf.ehcache.Ehcache` **class not found exception**.
    ```
    @Slf4j
    @Configuration
    @RequiredArgsConstructor
    @ConditionalOnBean(Ehcache.class)
    @ConditionalOnClass(Ehcache.class)
    public class EhcacheDelegationConfig implements BeanPostProcessor {
   
        private final Ehcache ehcache;
    
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    
            if (bean instanceof SuperHeroRepository) {
                log.info("*** Ehcache delegation with EhcacheSuperHeroRepositoryImpl class implementation in use ***");
                return new EhcacheSuperHeroRepositoryImpl((SuperHeroRepository) bean, ehcache);
            }
            return bean;
        }
    }
    ```
   
5. #### Ehcache Config class (Optional)
    This is optional class. If we configure this class in application then `Ehcache` will be enabled, also `EhcacheSuperHeroRepositoryImpl`'s implementation will be in use.<br>
    If we delete this class or comment the content of it then `BeanPostProcessor`'s `postProcessAfterInitialization()` method will not delegate the `EhcacheSuperHeroRepositoryImpl` class and requests will directly reach to DB
    without checking any cache data.
    
    ```
    @EnableCaching
    @Configuration
    public class EhcacheCacheConfig {
    
        @Bean
        public CacheManager getCustomCacheManager() {
            CacheManager cacheManager = CacheManager.create(getEhCacheConfiguration());
            cacheManager.addCache(ehcache());
            return cacheManager;
        }
    
        @Primary
        @Bean
        public Ehcache ehcache() {
            CacheConfiguration cacheConfig = new CacheConfiguration("my-cache", 1000)
                    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
                    .eternal(false)
                    .timeToLiveSeconds(3600)
                    .timeToIdleSeconds(3600);
            return new Cache(cacheConfig);
        }
    
        private net.sf.ehcache.config.Configuration getEhCacheConfiguration() {
            net.sf.ehcache.config.Configuration configuration = new net.sf.ehcache.config.Configuration();
            DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
            diskStoreConfiguration.setPath("java.io.tmpdir");
            configuration.addDiskStore(diskStoreConfiguration);
            return configuration;
        }
    }
    ```


    
### API Endpoints

- #### Super Hero CRUD Operations
    > **GET Mapping** http://localhost:8080/super-heroes  - Get all Super Heroes
    
    > **GET Mapping** http://localhost:8080/super-heroes/1  - Get Super Hero by ID
       
    > **GET Mapping** http://localhost:8080/super-heroes/in?ids=1,2,3  - Get all Super Heroes who has id 1, 2 or 3  
    
   
