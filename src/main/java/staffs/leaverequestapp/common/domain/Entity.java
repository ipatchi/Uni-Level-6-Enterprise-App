package staffs.leaverequestapp.common.domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Entity<T>{
    public static final String IDENTITY_CANNOT_BE_NULL = "Identity cannot be null";

    @EqualsAndHashCode.Include
    protected final Identity<T> id;

    public Entity(Identity<T> id) {
        if (id == null) throw new IllegalArgumentException(IDENTITY_CANNOT_BE_NULL);
        this.id = id;
    }

    public Identity<T> id() { return id; }
}
