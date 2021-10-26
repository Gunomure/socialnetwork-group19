package ru.skillbox.diplom.util.specification;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

public class SpecificationUtil<T> {

    public Specification<T> between(String key, LocalDateTime from, LocalDateTime to){
        LocalDateTime fromDate = Objects.isNull(from) ?
                LocalDateTime.of(1900, 1,1,0,0,0) : from;
        LocalDateTime toDate = Objects.isNull(to) ? LocalDateTime.now() : to;
        return (root, query, builder) -> builder.between(root.get(key), fromDate, toDate);
    }

    public Specification<T> equals(String key, String value) {
        return (root, query, builder)  -> equals(root.get(key), value).toPredicate(root, query, builder);

    }
    private Specification<T> equals(Path<String> key, String value) {
        return (root, query, builder) -> Objects.isNull(value) ? builder.conjunction() : builder.equal(builder.lower(key), value.toLowerCase());
    }

    public Specification<T> contains(String key, String value) {
        return (root, query, builder) -> contains(root.get(key), value).toPredicate(root, query, builder);
    }

    public Specification<T> contains(String key, Predicate.BooleanOperator booleanOperator, String... values){
        return (root, query, builder) -> contains(root.get(key), booleanOperator, values).toPredicate(root, query, builder);
    }

    private Specification<T> contains(Path<String> key, String value) {
        return (root, query, builder) -> Objects.isNull(value) ? builder.conjunction() : builder.like(builder.lower(key), "%" + value.toLowerCase() + "%");
    }

    private Specification<T> contains(Path<String> key, Predicate.BooleanOperator booleanOperator, String... values){
        return Arrays.stream(values)
                .map(v -> contains(key, v))
                .reduce((s1, s2) -> s1 = booleanOperator == Predicate.BooleanOperator.OR ? s1.or(s2) : s1.and(s2))
                .orElse(disjunctionOrConjunction(Predicate.BooleanOperator.AND));
    }

    private Specification<T> disjunctionOrConjunction(Predicate.BooleanOperator operator) {
        return (root, query, builder) -> operator == Predicate.BooleanOperator.AND ? builder.conjunction() : builder.disjunction();
    }

}
