package org.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// A custom annotation that marks methods that are internal, aka, should not be present
// in any deployment-ready code
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface InternalMethod { }
