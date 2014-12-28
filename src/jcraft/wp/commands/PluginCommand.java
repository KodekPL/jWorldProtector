package jcraft.wp.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginCommand {

    String[] args();

    int minArgs();

    int maxArgs();

    String permission();

    boolean requiresPlayer() default false;

    String usage();

}
