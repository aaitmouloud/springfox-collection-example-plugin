package springfox.collection.example.plugins;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.builders.PropertySpecificationBuilder;

import springfox.documentation.schema.CollectionType;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.schema.ScalarType;

import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import java.lang.reflect.AnnotatedElement;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CollectionExamplePluginTest {

    private static final String EXAMPLE = "my example";

    private CollectionExamplePlugin collectionExamplePlugin;

    @Mock
    private ModelPropertyContext modelPropertyContext;

    @Mock
    private PropertySpecificationBuilder builder;

    @Mock
    private AnnotatedElement annotatedElement;

    @BeforeEach
    void setUp() {

        collectionExamplePlugin = new CollectionExamplePlugin();
    }


    @Test
    void doesNothingWhenNotIsNotACollection() {

        when(modelPropertyContext.getSpecificationBuilder()).thenReturn(builder);

        final PropertySpecification propertySpecification = givenPropertyOfScalarType(EXAMPLE);
        when(builder.build()).thenReturn(propertySpecification);

        collectionExamplePlugin.apply(modelPropertyContext);

        verify(builder, never()).example(any());
    }


    @Test
    void doesNothingWhenExistingExampleIsNotAString() {

        when(modelPropertyContext.getSpecificationBuilder()).thenReturn(builder);

        final PropertySpecification propertySpecification = givenPropertyOfScalarType(new Object());
        when(builder.build()).thenReturn(propertySpecification);

        collectionExamplePlugin.apply(modelPropertyContext);

        verify(builder, never()).example(any());
    }


    @Test
    void doesNothingWhenExistingExampleIsAnEmptyString() {

        when(modelPropertyContext.getSpecificationBuilder()).thenReturn(builder);

        final PropertySpecification propertySpecification = givenPropertyOfScalarType("");
        when(builder.build()).thenReturn(propertySpecification);

        collectionExamplePlugin.apply(modelPropertyContext);

        verify(builder, never()).example(any());
    }


    @ParameterizedTest
    @EnumSource
    void fixesExampleWhenIsACollection(CollectionType collectionType) {

        when(modelPropertyContext.getSpecificationBuilder()).thenReturn(builder);

        final PropertySpecification propertySpecification = givenPropertyOfCollectionType(collectionType);
        when(builder.build()).thenReturn(propertySpecification);

        collectionExamplePlugin.apply(modelPropertyContext);

        verify(builder).example(Collections.singletonList(EXAMPLE));
    }


    private PropertySpecification givenPropertyOfCollectionType(CollectionType collectionType) {

        ModelSpecification modelSpecification = new ModelSpecificationBuilder().name("MyModel")
                .collectionModel(collectionSpecificationBuilder ->
                            collectionSpecificationBuilder.collectionType(collectionType)
                            .model(modelSpecificationBuilder ->
                                    modelSpecificationBuilder.scalarModel(ScalarType.STRING)).build())
                .build();

        return propertySpecificationWithExample(modelSpecification, EXAMPLE);
    }


    private PropertySpecification givenPropertyOfScalarType(Object example) {

        ModelSpecification modelSpecification = new ModelSpecificationBuilder().name("MyModel")
                .scalarModel(ScalarType.STRING)
                .build();

        return propertySpecificationWithExample(modelSpecification, example);
    }


    private PropertySpecification propertySpecificationWithExample(ModelSpecification modelSpecification,
        Object example) {

        return new PropertySpecificationBuilder("MyProperty").type(modelSpecification)
            .example(example)
            .build();
    }
}
