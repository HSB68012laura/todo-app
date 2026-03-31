package com.dwes.todo.task.model;

import com.dwes.todo.category.model.Category;
import com.dwes.todo.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import com.dwes.todo.tag.model.Tag;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@Entity
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Lob
    private String description;

    private boolean completed;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name= "fk_task_category"))
    private Category category;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "task_tag",
            joinColumns = @JoinColumn(name = "task_id"),
            foreignKey = @ForeignKey(name = "fk_task_tag_task"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            inverseForeignKey = @ForeignKey(name = "fk_task_tag_tag")
    )

    @Builder.Default
    @Setter(AccessLevel.NONE)
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name= "fk_task_user"))
    private User author;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Task task = (Task) o;
        return getId() != null && Objects.equals(getId(), task.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
