package rut.miit.sopcreditrating.entity;

import jakarta.persistence.*;

import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity {
    private UUID id;
    private boolean active;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    public UUID getId() {
        return id;
    }

    @Column(name = "active", nullable = false)
    public boolean isActive() { return active; }

    protected void setId(UUID id) {
        this.id = id;
    }
    public void setActive(boolean active) {this.active = active;}
}
