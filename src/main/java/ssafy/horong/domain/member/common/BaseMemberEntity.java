package ssafy.horong.domain.member.common;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모델 간 공통 사항 정의.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseMemberEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected MemberRole role;

    @Column(nullable = false)
    protected Boolean isDeleted = false;

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;

    public void delete(){
        this.isDeleted = true;
    }

    @PrePersist
    public abstract void prePersist();

    public MemberRole getRole() {
        return role;
    }

}