package com.midasteknologi.e_kyc_verification_summary.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_session")
@Data
public class UserSession {

    @Id
    private UUID id;

    private Long  userId;

    @OneToOne()
    @JoinColumn(name = "document_id")
    private UserDocument userDocument;

    @OneToOne()
    @JoinColumn(name = "video_id")
    private UserVideo userVideo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String attempts;

    @Column(name = "session_name")
    private String sessionName;

    @Column(name = "status")
    private String status;

    @Column(name = "reason")
    private String reason;

    private LocalDateTime sessionExpiry;

    private Boolean isActive;
}