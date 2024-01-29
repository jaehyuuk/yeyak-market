package com.myshop.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notifications")
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotiType type;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    private User toUser;

    @Column(name = "type_id")
    private Long typeId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Notification(Long id, NotiType type, User fromUser, User toUser, Long typeId, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.typeId = typeId;
        this.createdAt = createdAt;
    }
}