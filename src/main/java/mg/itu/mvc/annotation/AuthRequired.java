package mg.itu.mvc.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) // On ne peut l'utiliser que sur des méthodes
public @interface AuthRequired {
}