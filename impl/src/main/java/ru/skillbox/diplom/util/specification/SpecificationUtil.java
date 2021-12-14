package ru.skillbox.diplom.util.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.enums.FriendshipCode;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SpecificationUtil<T> {

    public Specification<T> between(String key, LocalDateTime from, LocalDateTime to) {
        LocalDateTime fromDate = Objects.isNull(from) ?
                LocalDateTime.of(1900, 1, 1, 0, 0, 0) : from;
        LocalDateTime toDate = Objects.isNull(to) ? LocalDateTime.now() : to;
        return (root, query, builder) -> builder.between(root.get(key), fromDate, toDate);
    }

    public Specification<T> between(String key, ZonedDateTime from, ZonedDateTime to){
        ZonedDateTime fromDate = Objects.isNull(from) ?
                ZonedDateTime.of(LocalDateTime.of(1900, 1,1,0,0,0), ZoneId.of("UTC")) : from;
        ZonedDateTime toDate = Objects.isNull(to) ? ZonedDateTime.now() : to;
        return (root, query, builder) -> builder.between(root.get(key), fromDate, toDate);
    }

    public Specification<T> equals(String key, String value) {
        return (root, query, builder) -> equals(makePath(root, key), value).toPredicate(root, query, builder);
    }

    private Specification<T> equals(Path<String> key, String value) {
        return (root, query, builder) -> Objects.isNull(value) ? builder.conjunction() : builder.equal(key, value);
    }

    public Specification<T> in(String key, List<Long> value) {
        return (root, query, builder) -> Objects.isNull(value) ?
                builder.conjunction() : builder.in(root.get(key)).value(value);
    }

    public Specification<T> belongsToCollection(String key, Collection<? extends BaseEntity> collection){
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get(key)).value(collection);
    }

    public Specification<T> containsTag(String[] tags){
        return (root, query, builder) -> {
            if (tags == null) return builder.conjunction();
            Join<T, PostToTag> join = root.join(Post_.POST_TO_TAGS);
            Join<PostToTag, Tag> secondJoin = join.join(PostToTag_.TAG_ID);
            return builder.in(secondJoin.get(Tag_.TAG)).value(Arrays.asList(tags));
        };
    }

    public Specification<T> notIn(String key, List<Long> value) {
        return (root, query, builder) -> Objects.isNull(value) ?
                builder.conjunction() : builder.not(builder.in(root.get(key)).value(value));
    }

    public Specification<T> equals(String key, Boolean value) {
        return (root, query, builder) -> builder.equal(root.get(key), value);
    }

    public Specification<T> equals(String field, String key, FriendshipCode status) {
        return (root, query, builder) -> builder.equal(root.get(field).get(key), status);
    }

    public Specification<T> equals(String field, String key, String value) {
        return (root, query, builder) -> equals(root.get(field).get(key), value).toPredicate(root, query, builder);
    }

    public Specification<T> equals(String key, Long value) {
        return (root, query, builder) -> equals(makePath(root, key), value).toPredicate(root, query, builder);
    }

    private Specification<T> equals(Path<String> key, Long value) {
        return (root, query, builder) -> Objects.isNull(value) ? builder.conjunction() : builder.equal(key, value);
    }

    public Specification<T> contains(String key, String value) {
        return (root, query, builder) -> contains(makePath(root, key), value).toPredicate(root, query, builder);
    }

    public Specification<T> contains(String field, String key, String value) {
        return (root, query, builder) -> contains(root.get(field).get(key), value).toPredicate(root, query, builder);
    }

    public Specification<T> contains(String key, Predicate.BooleanOperator booleanOperator, String... values) {
        return (root, query, builder) -> contains(makePath(root, key), booleanOperator, values).toPredicate(root, query, builder);
    }

    private Specification<T> contains(Path<String> key, String value) {
        return (root, query, builder) -> Objects.isNull(value) ? builder.conjunction() : builder.like(builder.lower(key), "%" + value.toLowerCase() + "%");
    }

    private Specification<T> contains(Path<String> key, Predicate.BooleanOperator booleanOperator, String... values) {
        return Arrays.stream(values)
                .map(v -> contains(key, v))
                .reduce((s1, s2) -> s1 = booleanOperator == Predicate.BooleanOperator.OR ? s1.or(s2) : s1.and(s2))
                .orElse(disjunctionOrConjunction(Predicate.BooleanOperator.AND));
    }

    private Specification<T> disjunctionOrConjunction(Predicate.BooleanOperator operator) {
        return (root, query, builder) -> operator == Predicate.BooleanOperator.AND ? builder.conjunction() : builder.disjunction();
    }

    private Path<String> makePath(Root<T> root, String key) {
        String[] path = key.split("\\.");
        Path<String> objectPath = root.get(path[0]);
        if (path.length > 1) {
            for (int i = 1; i < path.length; i++) {
                objectPath = objectPath.get(path[i]);
            }
        }
        return objectPath;
    }
}
