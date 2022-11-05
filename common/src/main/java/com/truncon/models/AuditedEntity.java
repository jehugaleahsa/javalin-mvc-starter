package com.truncon.models;

import com.truncon.auditing.AuditContext;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;

@MappedSuperclass
@EntityListeners(value = AuditedEntity.AuditedEntityListener.class)
public abstract class AuditedEntity extends Entity {
    @Column(name = "creation_date_time", nullable = false)
    private Instant creationDateTime;
    @JoinColumn(name = "created_by_user_id")
    @ManyToOne
    private User createdBy;
    @Column(name = "modification_date_time", nullable = false)
    private Instant modificationDateTime;
    @JoinColumn(name = "modified_by_user_id")
    @ManyToOne
    private User modifiedBy;

    protected AuditedEntity() {
    }

    public Instant getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Instant creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getModificationDateTime() {
        return modificationDateTime;
    }

    public void setModificationDateTime(Instant modificationDateTime) {
        this.modificationDateTime = modificationDateTime;
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public static final class AuditedEntityListener {
        @PrePersist
        public void prePersist(Object o) {
            if (!(o instanceof AuditedEntity)) {
                return;
            }
            AuditedEntity entity = (AuditedEntity) o;
            Instant now = Instant.now();
            User currentUser = AuditContext.getCurrent().getUser();
            entity.setCreationDateTime(now);
            entity.setCreatedBy(currentUser);
            entity.setModificationDateTime(now);
            entity.setModifiedBy(currentUser);
        }

        @PreUpdate
        public void preUpdate(Object o) {
            if (!(o instanceof AuditedEntity)) {
                return;
            }
            AuditedEntity entity = (AuditedEntity) o;
            entity.setModificationDateTime(Instant.now());
            entity.setModifiedBy(AuditContext.getCurrent().getUser());
        }
    }
}
