package ssafy.horong.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud.aws")
public record S3Properties (
        S3 s3,
        Region region,
        Credentials credentials
) {
    public record S3(
            String bucket
    ) { }

    public record Region(
            String staticRegion
    ) { }

    public record Credentials(
            String accessKey,
            String secretKey
    ) { }
}
