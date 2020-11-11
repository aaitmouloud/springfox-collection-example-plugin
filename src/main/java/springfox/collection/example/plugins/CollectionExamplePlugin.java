package springfox.collection.example.plugins;

import org.springframework.core.annotation.Order;

import org.springframework.stereotype.Component;

import org.springframework.util.StringUtils;

import springfox.documentation.schema.ModelSpecification;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Collections;


@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1)
@Component
public class CollectionExamplePlugin implements ModelPropertyBuilderPlugin {

    @Override
    public void apply(ModelPropertyContext context) {

        if (isCollectionProperty(context)) {
            final Object givenExample = context.getSpecificationBuilder().build().getExample();

            if (givenExample instanceof String && !StringUtils.isEmpty(givenExample)) {
                // Only update in case the example is non empty String value:
                // Examples for referenced objects are not present in this field and contain an empty String
                context.getSpecificationBuilder().example(Collections.singletonList(givenExample));
            }
        }
    }


    @Override
    public boolean supports(DocumentationType documentationType) {

        return true;
    }


    private static boolean isCollectionProperty(ModelPropertyContext context) {

        ModelSpecification modelSpecification = context.getSpecificationBuilder()
                .build()
                .getType();

        return modelSpecification != null && modelSpecification.getCollection().isPresent();
    }
}
