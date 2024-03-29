package com.example.eventsystem.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDateTime;
import java.util.Collection;

public enum Operator {

    EQUAL {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            if(request.getFieldType()==FieldType.OBJECT){
                value=request.getValue();
            }
            if (request.isOr()) {
                return cb.or(cb.equal(EntitySpecification.getExpression(root, request.getKey()), value), predicate);
            }
            return cb.and(cb.equal(EntitySpecification.getExpression(root, request.getKey()), value), predicate);
        }
    },
    NOT_EQUAL {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            if(request.getFieldType()==FieldType.OBJECT){
                value=request.getValue();
            }
            if (request.isOr()) {
                return cb.or(cb.notEqual(EntitySpecification.getExpression(root, request.getKey()), value), predicate);
            }
            return cb.and(cb.notEqual(EntitySpecification.getExpression(root, request.getKey()), value), predicate);
        }
    },

    LIKE {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate) {
            Expression<String> key = EntitySpecification.getExpression(root, request.getKey());
            if (request.isOr()) {
                return cb.or(cb.like(cb.upper(key), "%" + request.getValue().toString().toUpperCase() + "%"), predicate);
            }
            return cb.and(cb.like(cb.upper(key), "%" + request.getValue().toString().toUpperCase() + "%"), predicate);
        }
    },

    IN {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate) {
            Collection<Object> values = request.getValues();
            CriteriaBuilder.In<Object> inClause = cb.in(EntitySpecification.getExpression(root, request.getKey()));
            for (Object value : values) {
                inClause.value(request.getFieldType().parse(value.toString()));
            }
            if (request.isOr()) {
                return cb.or(inClause, predicate);
            }
            return cb.and(inClause, predicate);
        }
    },

    BETWEEN {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            Object valueTo = request.getFieldType().parse(request.getValueTo().toString());
            if (request.getFieldType() == FieldType.DATE) {
                LocalDateTime startDate = (LocalDateTime) value;
                LocalDateTime endDate = (LocalDateTime) valueTo;
                Expression<LocalDateTime> key = EntitySpecification.getExpression(root, request.getKey());
                if(request.isOr()){
                    return cb.or(cb.between(key, startDate, endDate), predicate);
                }
                return cb.and(cb.between(key, startDate, endDate));
            }

            if (request.getFieldType() != FieldType.CHAR && request.getFieldType() != FieldType.BOOLEAN) {
                Number start = (Number) value;
                Number end = (Number) valueTo;
                Expression<Number> key = EntitySpecification.getExpression(root, request.getKey());
                if (request.isOr()) {
                    return cb.or(cb.and(cb.ge(key, start), cb.le(key, end)), predicate);
                }
                return cb.and(cb.and(cb.ge(key, start), cb.le(key, end)), predicate);
            }

            return predicate;
        }
    };

    public abstract <T> Predicate build(Root<T> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate);

}
