package springfox.collection.example.plugins;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;


@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1)
@Component
public class CollectionExamplePlugin implements ModelPropertyBuilderPlugin {

    public void apply(ModelPropertyContext context) {
        final Optional<ApiModelProperty> apiModelProperty = extractAnnotation(context, ApiModelProperty.class);

        if (apiModelProperty.isPresent() && Collection.class.isAssignableFrom(getModelClass(context))) {
            final String givenExample = apiModelProperty.get().example();
            context.getBuilder().example(new Object[]{givenExample});

        }
    }


    public boolean supports(DocumentationType documentationType) {
        return true;
    }

    private static Class<?> getModelClass(ModelPropertyContext context) {
        String className = context.getBuilder().build().getQualifiedType();

        final int indexOfLt = className.indexOf('<');
        if (indexOfLt >= 0) {
            className = className.substring(0, indexOfLt);
        }

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return Object.class;
        }
    }

    private static <T extends Annotation> Optional<T> extractAnnotation(ModelPropertyContext context, Class<T> annotationClass) {
        if (context.getAnnotatedElement().isPresent() && context.getAnnotatedElement().get().isAnnotationPresent(annotationClass)) {
            return Optional.of(context.getAnnotatedElement().get().getAnnotation(annotationClass));

        }

        return Optional.empty();
    }
}
