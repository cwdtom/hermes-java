package com.qurong.hermes.annotation;

import com.qurong.hermes.HermesRegister;
import com.qurong.hermes.timer.Timer;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.*;

/**
 * 启用Hermes
 *
 * @author chenweidong
 * @since 1.2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({HermesRegister.class, Timer.class})
@EnableScheduling
public @interface EnableHermes {
}
