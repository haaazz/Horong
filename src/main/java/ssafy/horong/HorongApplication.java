package ssafy.horong;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableScheduling
@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"ssafy.horong"})
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@ConfigurationPropertiesScan("ssafy.horong.common.properties")  // 추가된 어노테이션

public class HorongApplication {

    public static void main(String[] args) {
        SpringApplication.run(HorongApplication.class, args);
    }

    @PostConstruct
    void started(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
