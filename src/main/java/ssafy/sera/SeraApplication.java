package ssafy.sera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableScheduling
@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"ssafy.sera"})
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class SeraApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeraApplication.class, args);
    }

}
