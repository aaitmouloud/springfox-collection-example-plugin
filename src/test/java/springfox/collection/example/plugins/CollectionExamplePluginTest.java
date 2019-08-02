package springfox.collection.example.plugins;

import io.swagger.annotations.ApiModelProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

import static com.google.common.base.Optional.of;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionExamplePluginTest {
    
    private static final String EXAMPLE = "my example";
    
    private CollectionExamplePlugin collectionExamplePlugin;
    
    @Mock
    private ModelPropertyContext modelPropertyContext;
    
    @Mock
    private ModelPropertyBuilder builder;
    
    @Mock
    private AnnotatedElement annotatedElement;
    
    @Mock
    private ApiModelProperty apiModelProperty;
    
    @BeforeEach
    void setUp() {
        collectionExamplePlugin = new CollectionExamplePlugin();
    }
    
    
    @Test
    void doesNothingWhenNotApiModelBuilderAnnotation() {
        when(modelPropertyContext.getAnnotatedElement()).thenReturn(of(annotatedElement));
        when(annotatedElement.isAnnotationPresent(ApiModelProperty.class)).thenReturn(false);
        
        collectionExamplePlugin.apply(modelPropertyContext);
        
        verifyZeroInteractions(builder);
    }
    
    
    @Test
    void doesNothingWhenNotIsNotACollection() {
        when(modelPropertyContext.getAnnotatedElement()).thenReturn(of(annotatedElement));
        when(modelPropertyContext.getBuilder()).thenReturn(builder);
        when(annotatedElement.isAnnotationPresent(ApiModelProperty.class)).thenReturn(true);
        when(annotatedElement.getAnnotation(ApiModelProperty.class)).thenReturn(apiModelProperty);
        
        final ModelProperty modelPropertyMock = mock(ModelProperty.class);
        when(builder.build()).thenReturn(modelPropertyMock);
        when(modelPropertyMock.getQualifiedType()).thenReturn(String.class.getCanonicalName());
        
        collectionExamplePlugin.apply(modelPropertyContext);
        
        verifyZeroInteractions(builder);
    }
    
    @Test
    void fixesExampleWhenIsACollection() {
        when(modelPropertyContext.getAnnotatedElement()).thenReturn(of(annotatedElement));
        when(modelPropertyContext.getBuilder()).thenReturn(builder);
        when(annotatedElement.isAnnotationPresent(ApiModelProperty.class)).thenReturn(true);
        when(annotatedElement.getAnnotation(ApiModelProperty.class)).thenReturn(apiModelProperty);
        
        final ModelProperty modelPropertyMock = mock(ModelProperty.class);
        when(builder.build()).thenReturn(modelPropertyMock);
        when(modelPropertyMock.getQualifiedType()).thenReturn(List.class.getCanonicalName());
        
        when(apiModelProperty.example()).thenReturn(EXAMPLE);
        
        collectionExamplePlugin.apply(modelPropertyContext);
        
        verify(builder).example(new Object[]{EXAMPLE});
    }
    
}
