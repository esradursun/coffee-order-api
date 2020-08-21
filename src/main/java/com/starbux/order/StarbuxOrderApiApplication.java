package com.starbux.order;

import com.starbux.order.model.Product;
import com.starbux.order.model.ProductType;
import com.starbux.order.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.hateoas.mediatype.collectionjson.CollectionJsonLinkDiscoverer;
import org.springframework.plugin.core.SimplePluginRegistry;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@Configuration
@EnableSwagger2
public class StarbuxOrderApiApplication implements CommandLineRunner {

    private final ProductRepository productRepository;

    public StarbuxOrderApiApplication(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(StarbuxOrderApiApplication.class, args);
    }

    @Primary
    @Bean
    public LinkDiscoverers discoverers() {
        List<LinkDiscoverer> plugins = new ArrayList<>();
        plugins.add(new CollectionJsonLinkDiscoverer());
        return new LinkDiscoverers(SimplePluginRegistry.create(plugins));
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @Override
    public void run(String... args) throws Exception {
        List<Product> productList = Arrays.asList(
                Product.builder().name("Black Coffee").price(4.0).productType(ProductType.DRINK).build(),
                Product.builder().name("Latte").price(5.0).productType(ProductType.DRINK).build(),
                Product.builder().name("Mocha").price(6.0).productType(ProductType.DRINK).build(),
                Product.builder().name("Tea").price(3.0).productType(ProductType.DRINK).build(),
                Product.builder().name("Milk").price(2.0).productType(ProductType.TOPPING).build(),
                Product.builder().name("Hazelnut syrup").price(3.0).productType(ProductType.TOPPING).build(),
                Product.builder().name("Chocolate sauce").price(5.0).productType(ProductType.TOPPING).build(),
                Product.builder().name("Lemon").price(2.0).productType(ProductType.TOPPING).build());

        productRepository.saveAll(productList);
    }
}
