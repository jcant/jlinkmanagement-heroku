package com.gmail.gm.jcant.JLinkManagement;

import com.gmail.gm.jcant.JLinkManagement.DomainRouting.JDomainRequestMappingHandlerMapping;
import com.gmail.gm.jcant.JLinkManagement.JPA.Article.JArticle;
import com.gmail.gm.jcant.JLinkManagement.JPA.Article.JArticleService;
import com.gmail.gm.jcant.JLinkManagement.JPA.Link.JLinkService;
import com.gmail.gm.jcant.JLinkManagement.JPA.RootLink.JRootLinkService;
import com.gmail.gm.jcant.JLinkManagement.JPA.User.JUser;
import com.gmail.gm.jcant.JLinkManagement.JPA.User.JUserDetailServiceImpl;
import com.gmail.gm.jcant.JLinkManagement.JPA.User.JUserRole;
import com.gmail.gm.jcant.JLinkManagement.JPA.User.JUserService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Date;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;


@SpringBootApplication
public class JLinkManagementApplication extends WebMvcConfigurationSupport{

    //@Value("${hibernate.dialect}")
    //private String sqlDialect;

    @Value("${hbm2ddl.auto}")
    private String hbm2dllAuto;

    @Value("${spring.datasource.url}")
	private String dbUrl;

	public static void main(String[] args) {
		SpringApplication.run(JLinkManagementApplication.class, args);
	}


	@Bean
	public CommandLineRunner demo(
		final JUserService userService, 
		final JLinkService linkService, 
		final JRootLinkService rlinkService,
		final JArticleService articleService) {
		return new CommandLineRunner() {
			@Override
			public void run(String... strings) throws Exception {
				JUser admin = new JUser("admin", "$2a$10$mvuMNa9iOkNJK1LyWLPj9uh.xaICWGjC78iRZkkdF9auHDjZLbjx.", JUserRole.ADMIN);
				JUser user = new JUser("user", "$2a$10$mvuMNa9iOkNJK1LyWLPj9uh.xaICWGjC78iRZkkdF9auHDjZLbjx.", JUserRole.USER);

				userService.addUser(admin);
				userService.addUser(user);

				JArticle art1 = new JArticle(new Date(), null, null, "Weclome!", 
					"It's good to see you here!<br>"+
					"This is a test project of the redirect platform. You can try the user and admin functionality, as well as the basic <strong>redirect functionality.</strong><br>"+
					"In view of hosting restrictions, the redirect works only on references of the type http://sitename.com/your_link</br>"+
					"</br>"+
					"You can register a new user or log in as an administrator or ordinaly user:", 
					admin);

				JArticle art2 = new JArticle(new Date(), null, null, "Admin", 
					"to Auth as an ADMIN, please input:<br>"+
					"<br>"+
					"login: <strong>admin</strong><br>"+
					"password: <strong>password</strong>", 
					admin);

				JArticle art3 = new JArticle(new Date(), null, null, "User", 
					"to Auth as an USER, please input:<br>"+
					"<br>"+
					"login: <strong>user</strong><br>"+
					"password: <strong>password</strong>", 
					admin);
				
				articleService.addArticle(art1);
				articleService.addArticle(art2);
				articleService.addArticle(art3);

//				linkService.addLink(new JLink("http://u1.short2.jca:8080/", "http://google.com", admin, new Date(), new Date()));
//				linkService.addLink(new JLink("http://u2.short2.jca:8080/", "http://gmail.com", admin, new Date(), new Date()));
//				linkService.addLink(new JLink("q3.com", "jcant.linkedin.com", admin, new Date(), new Date()));
//
//				linkService.addLink(new JLink("u1.com", "user.facebook.com", user, new Date(), new Date()));
//				linkService.addLink(new JLink("u2.com", "user.twitter.com", user, new Date(), new Date()));
//
//				rlinkService.addRootLink(new JRootLink("short2.jca:8080"));
//				rlinkService.addRootLink(new JRootLink("short3.jca:8080"));

				//non unique url - must throws an exception
				//linkService.addLink(new JLink("u2.com", "user.linkedin.com", user, new Date(), new Date()));
			}
		};
	}




    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
        return new JpaTransactionManager(emf);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory
            (DataSource dataSource, JpaVendorAdapter jpaVendorAdapter)
    {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter);
        entityManagerFactory.setJpaProperties(additionalProperties());
        entityManagerFactory.setPackagesToScan("com.gmail.gm.jcant.JLinkManagement");
        return entityManagerFactory;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        //adapter.setShowSql(true);
        //adapter.setDatabasePlatform(sqlDialect);
        //adapter.setDatabase(Database.POSTGRESQL);
    	//adapter.setGenerateDdl(true);

        return adapter;
    }
    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", hbm2dllAuto);
        return properties;
    }

    @Bean
    public UrlBasedViewResolver setupViewResolver() {
    	UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setPrefix("/pages/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);

        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	registry
        .addResourceHandler("/css/**")
        .addResourceLocations("/resources/css/");

    	registry
    	.addResourceHandler("/images/**")
        .addResourceLocations("/resources/images/");

    	registry
    	.addResourceHandler("/js/**")
        .addResourceLocations("/resources/js/");

    	registry
    	.addResourceHandler("/bootstrap/**")
        .addResourceLocations("/resources/bootstrap/");
    }


    @Bean
    public UserDetailsService userDetailsService(){
        return new JUserDetailServiceImpl();
    }

    @Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		RequestMappingHandlerMapping handlerMapping = new JDomainRequestMappingHandlerMapping();
		handlerMapping.setOrder(0);
		handlerMapping.setInterceptors(getInterceptors());
		return handlerMapping;
	}

    @Bean
	public DataSource dataSource()  {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
	}
}
