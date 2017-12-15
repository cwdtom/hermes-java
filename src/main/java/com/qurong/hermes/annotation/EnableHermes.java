package com.qurong.hermes.annotation;

import com.qurong.hermes.HermesRegister;
import com.qurong.hermes.timer.Timer;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({HermesRegister.class, Timer.class})
@EnableScheduling
public @interface EnableHermes {
}
