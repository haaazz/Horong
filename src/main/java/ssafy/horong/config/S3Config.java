package ssafy.horong.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import ssafy.horong.common.properties.S3Properties;

@Configuration
@RequiredArgsConstructor
public class S3Config {
    private final S3Properties s3Properties;

    // 기존 AmazonS3 클라이언트
    @Bean
    public AmazonS3 amazonS3Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
                s3Properties.credentials().accessKey(),
                s3Properties.credentials().secretKey()
        );
        return AmazonS3ClientBuilder.standard()
                .withRegion(s3Properties.region().staticRegion())
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(s3Properties.region().staticRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                s3Properties.credentials().accessKey(),
                                s3Properties.credentials().secretKey()
                        )))
                .build();
    }
}
