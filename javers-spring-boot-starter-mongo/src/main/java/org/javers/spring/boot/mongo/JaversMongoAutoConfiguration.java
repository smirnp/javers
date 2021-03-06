package org.javers.spring.boot.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.MappingStyle;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.mongo.MongoRepository;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.aspect.JaversAuditableRepositoryAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties({JaversProperties.class})
public class JaversMongoAutoConfiguration {

    @Autowired
    private JaversProperties javersProperties;

    @Autowired
    private MongoClient mongoClient;

    @Bean
    public Javers javers() {
        JaversRepository javersRepository = new MongoRepository(mongoClient.getDatabase(javersProperties.getDatabaseName()));

        return JaversBuilder.javers()
                .withListCompareAlgorithm(ListCompareAlgorithm.valueOf(javersProperties.getAlgorithm().toUpperCase()))
                .withMappingStyle(MappingStyle.valueOf(javersProperties.getMappingStyle().toUpperCase()))
                .withNewObjectsSnapshot(javersProperties.isNewObjectSnapshot())
                .withPrettyPrint(javersProperties.isPrettyPrint())
                .withTypeSafeValues(javersProperties.isTypeSafeValues())
                .registerJaversRepository(javersRepository)
                .build();
    }


    @Bean
    @ConditionalOnMissingBean
    public AuthorProvider authorProvider() {
        return new AuthorProvider() {
            public String provide() {
                return "unknown";
            }
        };
    }

    @Bean
    public JaversAuditableRepositoryAspect javersAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider) {
        return new JaversAuditableRepositoryAspect(javers, authorProvider);
    }
}
